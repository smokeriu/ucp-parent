A operator support read user define (simple) java code as a map function

# Config:

| key          | value                                                                                                                                                                                                                                                                                                                                                                                                 |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| codeFile     | The name of the code file. use --files to upload code file. if use client/local mode, user need to specify it as an absolute path.                                                                                                                                                                                                                                                                    |
| methodName   | The name of the method we invoked from spark.                                                                                                                                                                                                                                                                                                                                                         |
| returnName   | The name of the result.                                                                                                                                                                                                                                                                                                                                                                               |
| returnType   | The type of the result.                                                                                                                                                                                                                                                                                                                                                                               |
| inputColumns | An ordered list containing the names of the input parameters of the calling method. <br/>1. You can pass in just the name, at which point Ucp will assume that the parameter types correspond to each other.<br/>2. You can pass a combination of [name:type], where Ucp will format it according to type before calling the method.<br/>The above two methods do not support mixed use at this time. |

## note

- If it is a parameterless method, do not specify parameter `inputColumns`.

## Example

```
  {
    name: code
    parentNames: [test_in1]
    config {
      elementType: Operator
      pluginType: Code
      codeFile: "/path/to/test1.java"
      methodName: "hello"
      returnName: "hello"
      returnType: "string"
      inputColumns: ["id:string"]
    }
  }
```

# Java code:

Only one public method is required, no class is needed.

- If some packages need to be introduced via import, they must be placed at the top.
- If third-party packages are used, they need to be passed into Spark's Runtime environment by `--jars`.

## Example:

java code:

```java
import java.util.Map;

public String hello(String in){
    return "hello "+in;
}
```
