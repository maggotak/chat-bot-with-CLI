package chatbot;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Scanner;

public class SimpleBot extends AbstractBehavior<SimpleBot.Command> {
    private final static Scanner scanner = new Scanner(System.in);

    public interface Command {}

    public static class Refer implements Command {
        ActorRef<SimpleBot.Command> replyTo;

        public Refer(ActorRef<Command> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public enum Start implements Command {
        INSTANCE
    }

    final static class Message implements Command {
        private final String message;
        final ActorRef<SimpleBot.Command> replyTo;

        Message(String message, ActorRef<SimpleBot.Command> replyTo) {
            this.message = message;
            this.replyTo = replyTo;
        }

        @Override
        public String toString() {
            return message;
        }
    }


    public static Behavior<Command> create() {
        return Behaviors.setup(SimpleBot::new);
    }

    private final ActorRef<Message> toService;

    private SimpleBot(ActorContext<Command> context) {
        super(context);
        toService = getContext().spawn(Service.create(), "service");
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Message.class, this::onAnswer)
                .onMessage(Refer.class, ref -> onStart(ref.replyTo))
                .build();
    }

    private Behavior<Command> onStart(ActorRef<SimpleBot.Command> replyTo) {
        System.out.println("Please write your question");
        String message = scanner.nextLine();
        Message messageFromUser = new Message(message, replyTo);
        toService.tell(messageFromUser);
        return onSearch();
    }


    private Behavior<Command> onSearch() {
        return Behaviors.receive(Command.class)
                .onMessage(Message.class, this::onAnswer)
                .build();
    }

    private Behavior<Command> onAnswer(Message message) {
        System.out.println(message);
        System.out.println("Continue? Y/N");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("Y")) {
            return onStart(getContext().getSelf());
        } else if (answer.equalsIgnoreCase("N")) {
            return Behaviors.stopped();
        } else throw new IllegalArgumentException();
    }

}
