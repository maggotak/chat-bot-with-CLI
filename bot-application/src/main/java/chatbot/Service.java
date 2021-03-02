package chatbot;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Service extends AbstractBehavior<SimpleBot.Message>{

    public static Behavior<SimpleBot.Message> create() {
        return Behaviors.setup(Service::new);
    }


    private Service(ActorContext<SimpleBot.Message> context) {
        super(context);
    }

    @Override
    public Receive<SimpleBot.Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(SimpleBot.Message.class, this::answer)
                .build();
    }

    private Behavior<SimpleBot.Message> answer(SimpleBot.Message message) {
        String answer = Service.askService(message.toString());
        SimpleBot.Message messageToUser = new SimpleBot.Message(answer, message.replyTo);
        message.replyTo.tell(messageToUser);
        return this;
    }

    private static String askService(String message) {
        String url = String.format("http://localhost:8080/ask?question=%s", message);
        String text = "";
        try {
            URL obj = new URL(url.replace(' ', '+'));
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            text = response.toString();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}
