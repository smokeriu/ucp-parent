# JDBC Config

| key          | info                                                          |
|--------------|---------------------------------------------------------------|
| connectInfo  | Records the connection information required by JDBC. As a Key |
| otherOptions | The other options are at the same level as connectInfo        |

## connectInfo

| key            | info                                                                                                                           |
|----------------|--------------------------------------------------------------------------------------------------------------------------------|
| url            | jdbc url.                                                                                                                      |
| format         | If no url is specified, then you need to specify. Like: `jdbc:mysql://%s:%d/%s?%s`.<br/>Help to create url by given other info |
| host           | If no url is specified, then you need to specify.                                                                              |
| port           | If no url is specified, then you need to specify.                                                                              |
| dbName         | If no url is specified, then you need to specify.                                                                              |
| connectOptions | A key-value map. use to create url If no url is specified                                                                      |
| user           | user name                                                                                                                      |
| password       | user password                                                                                                                  |
| driver         | jdbc driver class name                                                                                                         |


## otherOptions

| key               | info                                                                                                                                                                  |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| saveMode          | If Plugin is used as Writer, you need to specify. overwrite/append support now. <br/>default is append.                                                               |
| readMode          | If Plugin is used as Reader, you need to specify. increment/single/distribute support now.<br/>default is single                                                      |
| distributeColumn  | when use increment/distribute readMode. need to specify. <br/>when increment, should specify a datetime column.<br/>when distraibute, should specify a numeric column |
| startTime/endTime | when use increment readMode. need to specify. Used to determine the reading range.                                                                                    |
| upper/lower       | when use distribute readMode. need to specify. Used to determine the reading range. upper is the maximum value, lower is the minimum value                            |
| extraOptions      | A key-value map. Added to Option when Spark reads or writes.                                                                                                          |

## example
```
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
```
