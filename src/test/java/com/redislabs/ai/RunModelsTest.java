package com.redislabs.ai;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.redislabs.redisai.Backend;
import com.redislabs.redisai.Device;
import com.redislabs.redisai.RedisAI;
import com.redislabs.redisai.Tensor;

import redis.clients.jedis.JedisPool;

public class RunModelsTest {
	private final JedisPool pool = new JedisPool();
	private final RedisAI client = new RedisAI(pool);
	
	@BeforeEach
	public void removeModels() {
		
	}

	@Test // PASSING
	public void testRunTensorFlowModel() {
		ClassLoader classLoader = getClass().getClassLoader();
		String model = classLoader.getResource("ai/graph.pb").getFile();
		client.setModel("model", Backend.TF, Device.CPU, new String[] { "a", "b" }, new String[] { "mul" }, model);

		client.setTensor("a", new float[] { 2, 3 }, new int[] { 2 });
		client.setTensor("b", new float[] { 3, 5 }, new int[] { 2 });

		assertTrue(client.runModel("model", new String[] { "a", "b" }, new String[] { "c" }));
		Tensor tensor = client.getTensor("c");
		float[] values = (float[]) tensor.getValues();
		float[] expected = new float[] { 6, 15 };
		assertEquals(2, values.length);
		assertArrayEquals(values, expected, (float) 0.1);
	}
	
	@Test // BROKEN
	public void testRunTorchScriptModel() {
		ClassLoader classLoader = getClass().getClassLoader();
		String model = classLoader.getResource("ai/iris.pt").getFile();
		
	    client.setModel("iris-torch", Backend.TORCH, Device.CPU, new String[] {"iris:in"}, new String[] {"iris:inferences", "iris:scores"}, model);
	    
	    // AI.TENSORSET iris:in FLOAT 2 4 VALUES 5.0 3.4 1.6 0.4 6.0 2.2 5.0 1.5
	    client.setTensor("iris:in", new double[][] {{5.0, 3.4, 1.6, 0.4}, {6.0, 2.2, 5.0, 1.5}}, new int[]{2, 4});
	    
	    // AI.MODELEXECUTE iris INPUTS 1 iris:in OUTPUTS 2 iris:inferences iris:scores
	    assertTrue(client.runModel("iris-torch", new String[] {"iris:in"}, new String[] {"iris:inferences", "iris:scores"}));
	    
	    Tensor in = client.getTensor("iris:in");
	    System.out.println(">>> IN shape --> " + Arrays.toString(in.getShape()));
	    System.out.println(">>> IN values --> " +  Arrays.toString((double[])in.getValues()));
	    
	    // AI.TENSORGET iris:inferences VALUES
	    Tensor inferences = client.getTensor("iris:inferences");
	    System.out.println(">>> INFERENCES shape --> " + Arrays.toString(inferences.getShape()));
	    System.out.println(">>> INFERENCES values --> " +  Arrays.toString((double[])inferences.getValues()));
	    
	     // AI.TENSORGET iris:scores VALUES
	    Tensor scores = client.getTensor("iris:scores");
	    System.out.println(">>> SCORES shape --> " + Arrays.toString(scores.getShape()));
	    System.out.println(">>> SCORES values --> " +  Arrays.toString((double[])scores.getValues()));
	}
	
	/*
	 java.lang.ClassCastException: class java.lang.Long cannot be cast to class [B (java.lang.Long and [B are in module java.base of loader 'bootstrap')
	at com.redislabs.redisai.DataType$2.toObject(DataType.java:47)
	at com.redislabs.redisai.Tensor.createTensorFromRespReply(Tensor.java:59)
	at com.redislabs.redisai.RedisAI.getTensor(RedisAI.java:182)
	at com.redislabs.ai.AppTest.testRunTorchScriptModel(AppTest.java:55)
	...
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
...
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	 */
}
