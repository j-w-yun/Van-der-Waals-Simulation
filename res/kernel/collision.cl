inline float dotVec(float px, float py, float pz, float px_, float py_, float pz_)
{
	return px*px_ + py*py_ + pz*pz_;
}

inline float lengthVec(float x, float y, float z)
{
	return sqrt(dotVec(x,y,z,x,y,z))+0.0000001;
}

__kernel void collisionKernel(__global float *particleData, int size, float passedTime)
{
	int j = get_global_id(0);

	float px = particleData[j*8+0];
	float py = particleData[j*8+1];
	float pz = particleData[j*8+2];
	float vx = particleData[j*8+3];
	float vy = particleData[j*8+4];
	float vz = particleData[j*8+5];
	float radius = particleData[j*8+6];
	float mass = particleData[j*8+7];

	//barrier(CLK_LOCAL_MEM_FENCE);
	for(int k = j+1; k < size; k++)
	{
		bool colliding = false;
		
		float px_ = particleData[k*8+0];
		float py_ = particleData[k*8+1];
		float pz_ = particleData[k*8+2];
		float vx_ = particleData[k*8+3];
		float vy_ = particleData[k*8+4];
		float vz_ = particleData[k*8+5];
		float radius_ = particleData[k*8+6];
		float mass_ = particleData[k*8+7];

		////////////   1   ////////////

		// deltaPosition
		float dpx = px_-px;
		float dpy = py_-py;
		float dpz = pz_-pz;

		// deltaPositionLengthSqSubR
		float dpLenSqSubR = dotVec(dpx,dpy,dpz,dpx,dpy,dpz);

		// sumRadii
		float sumR = (radius + radius_);
		dpLenSqSubR -= sumR * sumR;

		// movementVector
		float dvx = vx-vx_;
		float dvy = vy-vy_;
		float dvz = vz-vz_;

		// account for delta time
		dvx *= passedTime;
		dvy *= passedTime;
		dvz *= passedTime;

		// movementVectorLengthSq
		float dvLenSq = dotVec(dvx,dvy,dvz,dvx,dvy,dvz);

		// reject case #1
		if(dvLenSq < dpLenSqSubR)
		{
			colliding = false;
			break;
		}

		////////////   2   ////////////

		// movementVectorScalar
		float dvLen = lengthVec(dvx,dvy,dvz);
		float dvxScl = dvx / dvLen;
		float dvyScl = dvy / dvLen;
		float dvzScl = dvz / dvLen;

		// d
		float d = dotVec(dvxScl,dvyScl,dvzScl,dpx,dpy,dpz);

		// reject case #2
		if(d <= 0)
		{
			colliding = false;
			break;
		}

		////////////   3   ////////////

		// deltaPositionLengthSq
		float dpLenSq = dotVec(dpx,dpy,dpz,dpx,dpy,dpz);

		// f
		float f = dpLenSq - (d*d);

		// sumRadiiSquared
		float sumRSq = sumR*sumR;

		// reject case #3
		if(f >= sumRSq)
		{
			colliding = false;
			break;
		}

		////////////PROCEED////////////

		// t
		float t = sumRSq - f;

		// distance
		float distance = d - sqrt(t);

		// mag
		float magnitude = lengthVec(dvx,dvy,dvz);

		// reject case #4
		if(magnitude < distance)
		{
			colliding = false;
			break;
		}

		////////////  POC  ////////////

		// this.setPosition()
		float vLen = lengthVec(vx,vy,vz);
		float vxNorm = vx / vLen;
		float vyNorm = vy / vLen;
		float vzNorm = vz / vLen;
		particleData[j*8+0] = (px + (vxNorm * distance * passedTime));
		particleData[j*8+1] = (py + (vyNorm * distance * passedTime));
		particleData[j*8+2] = (pz + (vzNorm * distance * passedTime));
		// other.setPosition()
		float v_Len = lengthVec(vx_,vy_,vz_);
		float vx_Norm = vx_ / v_Len;
		float vy_Norm = vy_ / v_Len;
		float vz_Norm = vz_ / v_Len;
		particleData[k*8+0] = (px_ + (vx_Norm * distance * passedTime));
		particleData[k*8+1] = (py_ + (vy_Norm * distance * passedTime));
		particleData[k*8+2] = (pz_ + (vz_Norm * distance * passedTime));

		// n
		float dpLen = lengthVec(dpx,dpy,dpz);
		float nx = dpx / dpLen;
		float ny = dpy / dpLen;
		float nz = dpz / dpLen;

		// a1 & a2
		float a1 = dotVec(vx,vy,vz,nx,ny,nz);
		float a2 = dotVec(vx_,vy_,vz_,nx,ny,nz);

		// p
		float p = (2 * (a1-a2)) / (mass + mass_);

		// aVel
		float avx = vx - (nx * mass_ * p);
		float avy = vy - (ny * mass_ * p);
		float avz = vz - (nz * mass_ * p);
		// bVel
		float bvx = vx_ + (nx * mass * p);
		float bvy = vy_ + (ny * mass * p);
		float bvz = vz_ + (nz * mass * p);

		// this.setVelocity()
		particleData[j*8+3] = avx;
		particleData[j*8+4] = avy;
		particleData[j*8+5] = avz;
		// other.setVelocity()
		particleData[k*8+3] = bvx;
		particleData[k*8+4] = bvy;
		particleData[k*8+5] = bvz;
	}

	particleData[j*8+0] = px + vx*passedTime;
	particleData[j*8+1] = py + vy*passedTime;
	particleData[j*8+2] = pz + vz*passedTime;
}