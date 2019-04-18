package server;

import database.UpdateObject;

import java.util.HashMap;
import java.util.Optional;


/**
 * Class that wraps around a HashMap to control sending temperature
 * changes from the client to the master.
 */
public class TempController {

    private HashMap<Integer, UpdateObject> tempRanges;

    public TempController() {
        tempRanges = new HashMap<>();
    }

    public void changeTemperature(int id, UpdateObject update) {
        tempRanges.put(id, update);
    }

    public Optional<UpdateObject> getTemperatureChange(int id) {
        System.out.println(tempRanges);

        if (tempRanges.containsKey(id)) {
            UpdateObject newRanges = tempRanges.get(id);
            tempRanges.remove(id);

            return Optional.of(newRanges);
        } else {
            return Optional.empty();
        }
    }

    public HashMap<Integer, UpdateObject> getTempRanges() {
        return tempRanges;
    }

    public void setTempRanges(HashMap<Integer, UpdateObject> tempRanges) {
        this.tempRanges = tempRanges;
    }
}
