# Tracker

Library for tracking network connection and understanding where do network anomalies occur.

## How it works

We use publisher-subscriber pattern: you subscribe, it gives you notifications.

## Usage

Subscribe after created, unsubscribe after destroyed

```
class MainActivity(){
  val t = Tracker()
  fun handleNetworkConnection(payload) {
    print("I use payload", payload)
  }
  fun onCreate(){
    t.subscribe("Main Activity", TrackEvent.NETWORK_CONNECTION, handleNetworkConnection)
  }
  ...
  fun onDestroyed(){
    t.unsubscribe(handleNetworkConnection)
  }
}
```

how pull logs
```
class MainActivity() {
  val t = Tracker()
  fun init(){
    if(!t.isCleanedInThisMonth() && ZonedDateTime().now().toString().substring(4,6)){
      val file = t.pullLogs()
      //send to back-end somehow
      t.cleanLogs()
    }
  }
}
```
