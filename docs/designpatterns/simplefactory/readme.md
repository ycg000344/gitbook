# 概述



> todo

# talk is cheap, just coding.



## java

```java
interface Operation{

}
class OperationAdd implements Operation{
    
}
class OperationFactory{
    public static Operation createOperation(String operate) {
        Operation operation = null;
        switch (operate) {
            case "+":
                operation = new OperationAdd();
                break;
            case "-":
                // operation = new OperationAdd();
                break;
            case "*":
                // operation = new OperationAdd();
                break;
            case "/":
                // operation = new OperationAdd();
                break;
            default:
                break;
        }
        return operation;
    }
}
```



## go

> todo

