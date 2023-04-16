# Tracker 
 
Library for tracking network connection and understanding where do network anomalies occur. 
 
## How it works 
 
We use publisher-subscriber pattern: you subscribe, it gives you notifications. 
 
## Usage 
 
### Initialization 
 
Class takes 2 required arguments: activity's context and period of refreshing dates.   
```
private val context: Context = this 
val period: Long = 1000L 
val tracker:Tracker = Tracker(this, period) 
``` 
 
### Activity flow 
 
Subscribe after created, unsubscribe after destroyed. 
 
``` 
class MainActivity(): AppCompatActivity() { 
  private val context: Context = this 
  val period: Long = 1000L 
  val t:Tracker = Tracker(context, period) 
  fun handleNetworkConnection(payload) { 
    print("I use payload", payload) 
  } 
  fun onCreate(){ 
    t.subscribe("Main Activity", TrackerEvent.NETWORK_CONNECTION, handleNetworkConnection) 
  } 
  ... 
  fun onDestroyed(){ 
     
    // stop checker, remove subscriber 
    t.stop() 
    t.unsubscribe(handleNetworkConnection) 
     
    // or use - stop checker and remove all subscribers 
    t.clean() 
  } 
} 
```
 
### Pull logs 
 
Function pullLogs gives you file with logs in ".txt", that you can send to back-end, cleanLogs - clean logs. 
 
 ```
  ... 
  handleLogs () { 
    private val context: Context = this 
    val period: Long = 1000L 
    val t:Tracker = Tracker(context, period)     
     
    //heres also maybe your checks from back-end 
    val limit_logs = 100 
    if(t.countLogs() > limit_logs){ 
      val file: File = t.pullLogs() 
       
      //send on back-end somehow 
      ... 
       
      t.cleanLogs() 
    } 
  } 
 ```
 
### Handle paralel activities 
 
Use function changeNetwork() - the arg is class Payload.Network {activityName: String, dataUsage: Long}
