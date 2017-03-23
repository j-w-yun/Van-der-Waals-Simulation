package org.yoon_technology.gpu;

import static org.jocl.CL.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.jocl.*;
import org.yoon_technology.simulation.Particle;

public class OpenCL {

	private static cl_context context;
	private static cl_command_queue commandQueue;
	private static cl_kernel kernel;
	private static cl_mem outputMem;

	public static void main(String[] args) {

		float[] a = new float[100];
		for(int j = 0; j < 100; j++) {
			a[j] = j;
		}

		float[] returned = OpenCL.compute(a, 10.0f);
		System.out.println(Arrays.toString(returned));
	}

	static {
		initCL();
	}

	/**
	 * Initialize OpenCL: Create the context, the command queue and the kernel.
	 */
	private static void initCL() {
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;
		final int deviceIndex = 0;

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

		// Obtain a device ID
		cl_device_id devices[] = new cl_device_id[numDevices];
		clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
		cl_device_id device = devices[deviceIndex];

		// Create a context for the selected device
		context = clCreateContext(contextProperties, 1, new cl_device_id[] {device}, null, null, null);

		// Create a command-queue for the selected device
		cl_queue_properties properties = new cl_queue_properties();
		commandQueue = clCreateCommandQueueWithProperties(context, device, properties, null);

		// Program Setup
		String source = ProgramReader.readFile("collide");

		// Create the program
		cl_program cpProgram = clCreateProgramWithSource(context, 1, new String[] {source}, null, null);

		// Build the program
		clBuildProgram(cpProgram, 0, null, "-cl-mad-enable", null, null);

		// Create the kernel
		kernel = clCreateKernel(cpProgram, "collide", null);
	}

	public static float[] getCollisionData(ArrayList<Particle> particles, double deltaT) {
		float[] a_ = loadParticleData(particles);

		return compute(a_, (float)deltaT);
	}

	private static float[] loadParticleData(ArrayList<Particle> particles) {
		float[] a = new float[particles.size() * 7];
		int index = 0;
		for(Particle particle : particles) {
			a[index++] = (float)particle.getPosition().getX();
			a[index++] = (float)particle.getPosition().getY();
			a[index++] = (float)particle.getPosition().getZ();
			a[index++] = (float)particle.getVelocity().getX();
			a[index++] = (float)particle.getVelocity().getY();
			a[index++] = (float)particle.getVelocity().getZ();
			a[index++] = (float)particle.getRadius();
		}
		return a;
	}

	/**
	 * Execute the kernel function and read the resulting data
	 */
	private static float[] compute(float[] a, float deltaT) {
		if(a.length == 0)
			return null;
		// TODO
		outputMem = clCreateBuffer(context, CL_MEM_WRITE_ONLY, Sizeof.cl_float * a.length * a.length, null, null);

		cl_mem a_ = clCreateBuffer(context, CL_MEM_READ_ONLY, a.length * Sizeof.cl_float, null, null);
		clEnqueueWriteBuffer(commandQueue, a_, true, 0, a.length * Sizeof.cl_float, Pointer.to(a), 0, null, null);

		long globalWorkSize[] = new long[2];
		globalWorkSize[0] = a.length * a.length;
		globalWorkSize[1] = 7;

		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(outputMem));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(a_));
		clSetKernelArg(kernel, 2, Sizeof.cl_float, Pointer.to(new float[] {a.length} ));
		clSetKernelArg(kernel, 3, Sizeof.cl_float, Pointer.to(new float[] {deltaT} ));

		clEnqueueNDRangeKernel(commandQueue, kernel, 2, null, globalWorkSize, null, 0, null, null); // Execute the kernel

		// Read the data into an array
		float[] data = new float[a.length * a.length];
		clEnqueueReadBuffer(commandQueue, outputMem, CL_TRUE, 0, Sizeof.cl_float * a.length * a.length, Pointer.to(data), 0, null, null);
		return data;
	}
}