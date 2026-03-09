package ticket.booking.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainServices {
    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_BD_PATH= "src/main/java/ticket/booking/LocalDB/trains.json";

    public TrainServices() throws IOException{
        File users = new File(TRAIN_BD_PATH);
        trainList = objectMapper.readValue(users, new TypeReference<List<Train>>(){});
        // deserialize data;
    }
    public List<Train> search(String source, String detination){
        return trainList.stream().filter(train -> validTrain(train, source, detination)).collect(Collectors.toList());
    }
    public void addTrain(Train newTrain){
        //check if a train with the same trainId already exists
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();
        if(existingTrain.isPresent()){
            // if a train with the same trainId exists update a new train
            updateTrain(newTrain);
        }else{
            //otherwise, add the new train to the list
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }
    public void updateTrain(Train updateTrain){
        // find the index of the train the same trainId
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updateTrain.getTrainId()))
                .findFirst();
        if (index.isPresent()){
            // If found, replace the existing train with the updated one
            trainList.set(index.getAsInt(), updateTrain);
            saveTrainListToFile();
        }else{
            // If not found, treat it as adding a new train
            addTrain(updateTrain);
        }
    }
    private void saveTrainListToFile(){
        try {
            objectMapper.writeValue(new File(TRAIN_BD_PATH),trainList);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private boolean validTrain(Train train, String source, String destination){
        List<String> stationOrder = train.getStations();
        int sourceIndex = stationOrder.indexOf(source.toUpperCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());
        return  sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }

}
