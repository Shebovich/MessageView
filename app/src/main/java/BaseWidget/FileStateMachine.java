package BaseWidget;

import com.okbtsp.messenger.FiniteStateMachine.IState;

import java.util.HashMap;
import java.util.Map;

public class FileStateMachine<P extends IState> {
    Map<String, P> statesMap = new HashMap<>();
    public FileStateMachine(){

    }
    public void addState(P state){
        statesMap.put(state.getStateName(),state);
    }
    public void initState(String stateName){
        if (statesMap.containsKey(stateName)){
            statesMap.get(stateName).initState();
        }
    }

}
