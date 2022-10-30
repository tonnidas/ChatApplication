package edu.baylor.ecs;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Message {
    //  Message types: 0 = regular message, 1 = set initial name,
    //  2 = set online, 3 = set do not disturb, 4 = refresh client list
    private Integer type;
    private String sender;
    private String content;
    private List<ClientRecord> clients;
}
