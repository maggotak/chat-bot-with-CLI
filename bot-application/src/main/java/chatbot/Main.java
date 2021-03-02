package chatbot;


import akka.actor.typed.ActorSystem;

public class Main {
    public static void main(String[] args) {
        ActorSystem<SimpleBot.Command> ubu = ActorSystem.create(MainBot.create(), "MainBot");
        ubu.tell(SimpleBot.Start.INSTANCE);
    }
}
