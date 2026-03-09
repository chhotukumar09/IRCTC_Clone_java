package ticket.booking.Services;
import  ticket.booking.util.UserServiceUtil;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.util.UserServiceUtil;


public class UserBookingServices {
    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();
    public static final String USERs_PATH = "src/main/java/ticket/booking/LocalDB/User.json";

    public UserBookingServices(User use1) throws IOException {
        this.user = use1;
        loadUserListFromFile();
    }
    public UserBookingServices() throws IOException{
        loadUserListFromFile();
    }
    private void loadUserListFromFile() throws IOException{
        userList = objectMapper.readValue(new File(USERs_PATH), new TypeReference<List<User>>() {});
    }
    public boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return  foundUser.isPresent();
    }
    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserLIstToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }
    private void saveUserLIstToFile() throws IOException{
        File userFile = new File(USERs_PATH);
        objectMapper.writeValue(userFile, userList);
    }
    public void fetchBooking(){
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if (userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }
    // todo: Complete this function
    public Boolean cancelBooking(String ticketId){

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId;  //Because strings are immutable
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        }else{
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }
    public List<Train> getTrains(String source, String destination) {
        try {
            TrainServices trainServices = new TrainServices();
            return trainServices.search(source, destination);
        } catch (IOException ex) {
            return new ArrayList<>();
        }
    }
    public  List<List<Integer>> featchSeat(Train train){
        return train.getSeats();
    }
    public Boolean bookTrainsSeaats(Train train, int row, int seat){
        try{
            TrainServices trainServices = new TrainServices();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()){
                if (seats.get(row).get(seat) == 0){
                    seats.get(row).set(seat, 1);
                    trainServices.addTrain(train);
                    return true; // Booking successfull
                }else {
                    return false; // seat is already booked
                }
            }else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }


}

