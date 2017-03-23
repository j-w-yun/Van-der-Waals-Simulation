package org.yoon_technology.gpu;

import static org.jocl.CL.*;

import java.nio.*;
import java.util.ArrayList;

import org.jocl.*;
import org.yoon_technology.simulation.Particle;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Collision {

	// Singleton
	private static volatile Collision instance;

	private cl_context context;
	private cl_device_id device;
	private cl_command_queue commandQueue;
	private cl_kernel kernel;
	// Shared virtual memory
	private Pointer svm;
	private FloatBuffer inBuffer;
	// Input data
	private int inSize;
	private final int partsIn = 8;

	public static Collision getInstance() {
		return instance == null ? instance = new Collision() : instance;
	}

	public Collision() {
		inSize = 0;
		String programSource = ProgramReader.readFile("collision");
		initCL(programSource);
	}

	public synchronized void compute(ArrayList<Particle> particles, double passedTime) {
		if(particles.size() < 2)
			return;

		if(particles.size() != inSize) {
			inSize = particles.size();

			// {particle data1, particle data2, ...}
			svm = clSVMAlloc(context, CL_MEM_SVM_FINE_GRAIN_BUFFER | CL_MEM_SVM_ATOMICS , Sizeof.cl_float*(inSize*partsIn), 0);
			// Get buffer
		}
		inBuffer = svm.getByteBuffer(0, Sizeof.cl_float*(inSize*partsIn)).order(ByteOrder.nativeOrder()).asFloatBuffer();

		// Modify buffer
		parseParticles(particles);

		// Execute
		try {
			clEnqueueSVMUnmap(commandQueue, svm, 0, null, null);
			clSetKernelArgSVMPointer(kernel, 0, svm);
			clSetKernelArg(kernel, 1, Sizeof.cl_int, Pointer.to(new int[] {particles.size()}));
			clSetKernelArg(kernel, 2, Sizeof.cl_float, Pointer.to(new float[] {(float)passedTime}));
			clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, new long[] {inSize}, null, 0, null, null);
			clEnqueueSVMMap(commandQueue, CL_TRUE, CL_MAP_WRITE_INVALIDATE_REGION, svm, Sizeof.cl_float*(inSize*partsIn), 0, null, null);
			clFinish(commandQueue);
		} catch (Exception e) {
			shutdown();
		}

		// Modify particles
		parseResults(particles);

	}

	private void parseParticles(ArrayList<Particle> particles) {
		synchronized(inBuffer) {
			int index = 0;
			for(Particle particle : particles) {
				inBuffer.put(index++, (float)particle.getPosition().getX());
				inBuffer.put(index++, (float)particle.getPosition().getY());
				inBuffer.put(index++, (float)particle.getPosition().getZ());
				inBuffer.put(index++, (float)particle.getVelocity().getX());
				inBuffer.put(index++, (float)particle.getVelocity().getY());
				inBuffer.put(index++, (float)particle.getVelocity().getZ());
				inBuffer.put(index++, (float)particle.getRadius());
				inBuffer.put(index++, (float)particle.getMass());
			}
		}
	}

	private synchronized void parseResults(ArrayList<Particle> particles) {
		synchronized(inBuffer) {
			int index = 0;
			for(Particle particle : particles) {
				float px = inBuffer.get(index++);
				float py = inBuffer.get(index++);
				float pz = inBuffer.get(index++);
				particle.getPosition().setX(px);
				particle.getPosition().setY(py);
				particle.getPosition().setZ(pz);

				float vx = inBuffer.get(index++);
				float vy = inBuffer.get(index++);
				float vz = inBuffer.get(index++);
				particle.getVelocity().setX(vx);
				particle.getVelocity().setY(vy);
				particle.getVelocity().setZ(vz);

				index+= 2; // Skip chaning radius and mass
			}
		}
	}

	public static void shutdown() {
		try {
			synchronized(instance) {
				// Release kernel, program, and memory objects
				SVMFreeFunction callback = new SVMFreeFunction() {
					@Override
					public void function(cl_command_queue queue, int num_svm_pointers, Pointer[] svm_pointers, Object user_data) {
						System.out.println("Callback for freeing " + num_svm_pointers + " SVM pointers, user data is " + user_data);
					}
				};
				Object userData = "SampleUserData";
				clEnqueueSVMFree(instance.commandQueue, 1, new Pointer[] {instance.svm}, callback, userData, 0, null, null);
				clFinish(instance.commandQueue);
				clReleaseKernel(instance.kernel);
				clReleaseCommandQueue(instance.commandQueue);
				clReleaseContext(instance.context);
			}
		} catch (Exception e) {}
	}

	/**
	 * Default OpenCL initialization of the devices, context, command queue,
	 * program and kernel.
	 */
	private void initCL(String kernelString) {
		// The platform and device type that will be used
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;

		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		// Obtain the number of platforms
		int numPlatformsArray[] = new int[1];
		clGetPlatformIDs(0, null, numPlatformsArray);
		int numPlatforms = numPlatformsArray[0];

		// Obtain a platform ID
		cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
		clGetPlatformIDs(platforms.length, platforms, null);
		cl_platform_id platform = platforms[platformIndex];

		// Initialize the context properties
		cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		int numDevicesArray[] = new int[1];
		clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
		int numDevices = numDevicesArray[0];

		// Obtain the all device IDs
		cl_device_id allDevices[] = new cl_device_id[numDevices];
		clGetDeviceIDs(platform, deviceType, numDevices, allDevices, null);

		// Find the first device that supports OpenCL 2.0
		for (cl_device_id currentDevice : allDevices) {
			String deviceName = getString(currentDevice, CL_DEVICE_NAME);
			float version = getOpenCLVersion(currentDevice);
			if (version >= 2.0) {
				System.out.println("Using device " + deviceName + ", version " + version);
				device = currentDevice;
				break;
			} else {
				System.out.println("Skipping device " + deviceName + ", version " + version);
			}
		}
		if (device == null) {
			System.out.println("No OpenCL 2.0 capable device found");
			System.exit(1);
		}

		// Create a context
		context = clCreateContext(contextProperties, 1, new cl_device_id[] {device}, null, null, null);

		// Create the command queue
		cl_queue_properties properties = new cl_queue_properties();
		commandQueue = clCreateCommandQueueWithProperties(context, device, properties, null);

		// Create the program from the source code
		cl_program program = clCreateProgramWithSource(context, 1, new String[] {kernelString}, null, null);

		// Build the program. It's important to specify the -cl-std=CL2.0 build parameter here!
		clBuildProgram(program, 0, null, "-cl-std=CL2.0", null, null);

		// Create the kernel
		kernel = clCreateKernel(program, "collisionKernel", null);

		clReleaseProgram(program);
	}

	/**
	 * Returns the OpenCL version of the given device, as a float value
	 *
	 * @param device
	 *            The device
	 * @return The OpenCL version
	 */
	private static float getOpenCLVersion(cl_device_id device) {
		String deviceVersion = getString(device, CL_DEVICE_VERSION);
		String versionString = deviceVersion.substring(7, 10);
		float version = Float.parseFloat(versionString);
		return version;
	}

	/**
	 * Returns the value of the device info parameter with the given name
	 *
	 * @param device
	 *            The device
	 * @param paramName
	 *            The parameter name
	 * @return The value
	 */
	private static String getString(cl_device_id device, int paramName) {
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		clGetDeviceInfo(device, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int) size[0]];
		clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length - 1);
	}

}