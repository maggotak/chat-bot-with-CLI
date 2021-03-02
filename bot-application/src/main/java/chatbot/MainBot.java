package chatbot;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class MainBot extends AbstractBehavior<SimpleBot.Command> {

    private final ActorRef<SimpleBot.Command> simpleBot;

    public static Behavior<SimpleBot.Command> create() {
        return Behaviors.setup(chatbot.MainBot::new);
    }

    private MainBot(ActorContext<SimpleBot.Command> context) {
        super(context);
        simpleBot = context.spawn(SimpleBot.create(), "simpleBot");
    }

    @Override
    public Receive<SimpleBot.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(SimpleBot.Command.class, this::onMessage)
                .onSignal(Terminated.class, this::onTerminated)
                .build();
    }

    private Behavior<SimpleBot.Command> onMessage(SimpleBot.Command command) {
        ActorRef<SimpleBot.Command> replyTo = getContext().spawn(SimpleBot.create(), "mainBot");
        simpleBot.tell(new SimpleBot.Refer(replyTo));
        getContext().watch(replyTo);
        return this;
    }


    private Behavior<SimpleBot.Command> onTerminated(Terminated terminated) {
        return Behaviors.stopped();
    }
}
