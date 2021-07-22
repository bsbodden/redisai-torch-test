# TorchScritp Model Execution Example with JRedisGraph Reproducer

## Issue/Exception with retrieving output tensors

The test class `RunModelsTest` has two test methods:

🟢 `testRunTensorFlowModel`: original method from JRedisAI test suite - *PASS*
🔴 `testRunTorchScriptModel`: the method in question - *FAIL*

## Using RedisAI docker image edge

`docker run -p 6379:6379 --name redisai redislabs/redisai:edge-cpu`

## Run with
`./mvnw clean verify`

The method loads a TorchScript pre-trained model that was created using `https://github.com/redis-developer/redisai-iris`

```
[ERROR] testRunTorchScriptModel  Time elapsed: 0.081 s  <<< ERROR!
java.lang.ClassCastException: java.lang.Long incompatible with [B
	at com.redislabs.ai.RunModelsTest.testRunTorchScriptModel(RunModelsTest.java:63)
```

The error happens on this line:

```
// AI.TENSORGET iris:inferences VALUES
Tensor inferences = client.getTensor("iris:inferences"); // 💥
```