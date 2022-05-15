Provide implementation-independent generic entity definitions.

## Element

The minimum unit of operation must be specified in the configuration as follows:

- name:
  - It is a string that defines the name of the element on a virtual canvas. 
  - In a set of elements, need to ensure its uniqueness.
  - Case-sensitive
- parentNames:
  - It is an array that defines the other elements that the element depends on when it runs, and when it does not depend on any element, it is not necessary to provide this configuration
- config:
  - Additional configuration of the component.It requires at least two of the following configurations:
    - elementType: Reader/Writer/Operator.Defined in the ElementType enumeration. Case-sensitive.
    - pluginType: Plugin types, such as Jdbc.
    - Other configurations needed for Plugin.

### example

```
elements = [
  {
    name: test
    parentNames: [test1, test2]
    config {
      elementType: Reader
      pluginType: Jdbc
      connectInfo {
        url: "jdbc:mysql://{ip}:{port}/test"
        driver: "com.mysql.cj.jdbc.Driver"
        tbName: "payment"
        user: "root"
        password: "root"
      }
      readMode: single
    }
  }
]
```

## ClientConfig

The parameters necessary to generate the Job.

- submitPrefix:
  - Currently, we submit Jobs via shell commands, so we need to specify the engine command.
  - Spark: `spark-submit`
  - Flink: `flink`
- engineConfig:
  - The configuration required for the engine. Depends on the specific implementation of the engine.


### example
```
env {
  submitPrefix: "spark-submit"
  engineConfig: {
    master: "local[2]"
    executorCores: 1
    executorMem: 2G
  }
}
```

## JobConfig

Some Job's configuration information.

- jobLevel:
  - Dev/Release. Defined in the JobLevel enumeration. Case-sensitive.
    - Release Mode.No validate/check work will be performed.
- jobMode:
  - Batch/Stream/StructStream. Defined in the JobMode enumeration. Case-sensitive.