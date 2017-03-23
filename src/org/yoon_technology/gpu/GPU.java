package org.yoon_technology.gpu;

import static org.jocl.CL.*;
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_NAME;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_DEVICE_VERSION;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetDeviceInfo;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;
import org.jocl.cl_queue_properties;

public class GPU {

	private static String programSource;
	private static cl_context context;
	private static cl_command_queue commandQueue;
	private static cl_program program;
	private static cl_kernel kernel;
	private static cl_device_id device;
	private static cl_mem srcMemA;
	private static cl_mem srcMemB;
	private static cl_mem srcMemC;
	private static cl_mem outputMem;

	static {
		programSource = ProgramReader.readFile("length");
		initCL();
	}

	public static void main(String[] args) {
		// Create input data
		int n = 1000000;
		float[] a = new float[n];
		float[] b = new float[n];
		float[] c = new float[n];
		for (int j = 0; j < n; j++) {
			a[j] = j;
			b[j] = j;
			c[j] = j;
		}

		float[] result = length(a, b, c);
		double[] answer = new double[n];

		boolean pass = true;
		double e = 0.000001;
		for (int j = 0; j < n; j++) {
			double num = (double)j;
			answer[j] = Math.sqrt(num*num+num*num+num*num);
			if(result[j] < answer[j] * (1-e) || result[j] > answer[j] * (1+e)) {
				System.out.println(result[j] + " != " + answer[j]);
				pass = false;
				break;
			}
		}
		System.out.println(pass ? "PASS" : "FAIL");
		shutdown();
	}

	public synchronized static float[] length(float[] x, float[] y, float[] z) {

		float[] outputArray = new float[x.length];

		srcMemA = clCreateBuffer(context,
				CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR,
				Sizeof.cl_float * x.length, Pointer.to(x), null);
		srcMemB = clCreateBuffer(context,
				CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR,
				Sizeof.cl_float * x.length, Pointer.to(y), null);
		srcMemC = clCreateBuffer(context,
				CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR,
				Sizeof.cl_float * x.length, Pointer.to(z), null);
		outputMem = clCreateBuffer(context,
				CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_float * x.length, Pointer.to(outputArray), null);

		length(x.length, srcMemA, srcMemB, srcMemC, outputMem);

		clEnqueueWriteBuffer(commandQueue, srcMemA, CL_TRUE, 0,
				x.length * Sizeof.cl_float, Pointer.to(x),
				0, null, null);
		clEnqueueWriteBuffer(commandQueue, srcMemB, CL_TRUE, 0,
				x.length * Sizeof.cl_float, Pointer.to(y),
				0, null, null);
		clEnqueueWriteBuffer(commandQueue, srcMemC, CL_TRUE, 0,
				x.length * Sizeof.cl_float, Pointer.to(z),
				0, null, null);
		clEnqueueReadBuffer(commandQueue, outputMem, CL_TRUE, 0,
				x.length * Sizeof.cl_float, Pointer.to(outputArray),
				0, null, null);

		clReleaseMemObject(srcMemA);
		clReleaseMemObject(srcMemB);
		clReleaseMemObject(srcMemC);
		clReleaseMemObject(outputMem);

		return outputArray;
	}

	private static void length(int inputLength, cl_mem srcMemA, cl_mem srcMemB, cl_mem srcMemC, cl_mem outputMem) {
		int a = 0;
		clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemA));
		clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemB));
		clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemC));
		clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(outputMem));

		clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
				new long[] {inputLength}, null, 0, null, null);
	}

	public static void shutdown() {
		try {
			clReleaseKernel(kernel);
			clReleaseProgram(program);
			clReleaseCommandQueue(commandQueue);
			clReleaseContext(context);
		} catch(Exception e) {}
	}

	private static void initCL() {
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
		for(cl_device_id currentDevice : allDevices) {
			String deviceName = getString(currentDevice, CL_DEVICE_NAME);
			float version = getOpenCLVersion(currentDevice);
			if(version >= 2.0) {
				//				System.out.println("Using device " + deviceName + ", version " + version);
				device = currentDevice;
				break;
			} else {
				System.out.println("Skipping device " + deviceName + ", version " + version);
			}
		}

		if(device == null) {
			System.out.println("No OpenCL 2.0 capable device found");
			System.exit(1);
		}

		// Create a context
		context = clCreateContext(
				contextProperties, 1, new cl_device_id[]{ device },
				null, null, null);

		// Create the command queue
		cl_queue_properties properties = new cl_queue_properties();
		commandQueue = clCreateCommandQueueWithProperties(context, device, properties, null);

		// Create the program from the source code
		program = clCreateProgramWithSource(context, 1, new String[]{ programSource }, null, null);

		// Build the program. It's important to specify the
		// -cl-std=CL2.0
		// build parameter here!
		clBuildProgram(program, 0, null, "-cl-std=CL2.0", null, null);

		// Create the kernel
		kernel = clCreateKernel(program, "sampleKernel", null);

		clReleaseProgram(program);
	}

	private static float getOpenCLVersion(cl_device_id device) {
		String deviceVersion = getString(device, CL_DEVICE_VERSION);
		String versionString = deviceVersion.substring(7, 10);
		float version = Float.parseFloat(versionString);
		return version;
	}

	private static String getString(cl_device_id device, int paramName) {
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		clGetDeviceInfo(device, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int)size[0]];
		clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length-1);
	}
}
