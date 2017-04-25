### ReducerBuilder
ReducerBuilder needs a specific class registered. No inheritance and interfaces. It's done
due to the fact, that for example some class might implement multiple interfaces, but each Action
must have Reducer (playing with inheritance here can possible lead to bad and unexpected behaviour)

Cannot register interfaces

Nonetheless, a simple hierarchy tree can be registered, so:
```java
class MyAction implements Action {}
class MyAction_1 extends MyAction {}
class MyAction_2 extends MyAction {}
```
we _can_ register `MyAction` Reducer to handle both `MyAction_1` & `MyAction_2`


### MiddlewareBuilder
Unlike ReducerBuilder allows registering generic classes. For example we have such Actions:
```java
interface ModifyAction extends Action {}
class AddAction implements ModifyAction {}
class RemoveAction implements ModifyAction {}
```
and here we easily register a generic Middleware to be triggered when an action of registered type is dispatched