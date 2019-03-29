package me.brucephillips;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        var result =
                Stream.of(
                        // Guest(String name, boolean participating, Integer participantsNumber)
                        new Guest("Marco", true, 3),
                        new Guest("David", false, 2),
                        new Guest("Roger",true, 6))
                        .collect(Collectors.teeing(
                                // first collector, we select only who confirmed the participation
                                Collectors.filtering(Guest::isParticipating,
                                        // whe want to collect only the first name in a list
                                        Collectors.mapping(o -> o.name, Collectors.toList())),
                                // second collector, we want the total number of participants
                                Collectors.summingInt(Guest::getParticipantsNumber),
                                // we merge the collectors in a new Object,
                                // the values are implicitly passed
                                EventParticipation::new
                        ));
        System.out.println(result);


        var result2 =
                Stream.of("Devoxx", "Voxxed Days", "Code One", "Basel One",
                        "Angular Connect")
                        .collect(Collectors.teeing(
                                // first collector
                                Collectors.filtering(n -> n.contains("xx"), Collectors.toList()),
                                // second collector
                                Collectors.filtering(n -> n.endsWith("One"), Collectors.toList()),
                                // merger - automatic type inference doesn't work here
                                (List<String> list1, List<String> list2) -> List.of(list1, list2)
                        ));
        System.out.println(result2);

        var result3 =
                Stream.of(5, 12, 19, 21)
                        .collect(Collectors.teeing(
                                // first collector
                                Collectors.counting(),
                                // second collector
                                Collectors.summingInt(n -> Integer.valueOf(n.toString())),
                                // merger: (count, sum) -> new Result(count, sum);
                                Result::new
                        ));
        System.out.println(result3); // -> {count=4, sum=57}

    }
}

class Guest {
    public String name;
    private boolean participating;
    private Integer participantsNumber;
    public Guest(String name, boolean participating,
                 Integer participantsNumber) {
        this.name = name;
        this.participating = participating;
        this.participantsNumber = participantsNumber;
    }
    public boolean isParticipating() {
        return participating;
    }
    public Integer getParticipantsNumber() {
        return participantsNumber;
    }


}
class EventParticipation {
    private List<String> guestNameList;
    private Integer totalNumberOfParticipants;
    public EventParticipation(List<String> guestNameList,
                              Integer totalNumberOfParticipants) {
        this.guestNameList = guestNameList;
        this.totalNumberOfParticipants = totalNumberOfParticipants;
    }
    @Override
    public String toString() {
        return "EventParticipation { " +
                "guests = " + guestNameList +
                ", total number of participants = " + totalNumberOfParticipants +
                " }";
    }
}

class Result {
    private Long count;
    private Integer sum;
    public Result(Long count, Integer sum) {
        this.count = count;
        this.sum = sum;
    }
    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", sum=" + sum +
                '}';
    }
}
