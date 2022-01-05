package be.generativelasers.output.etherdream;

public enum EtherdreamResponseStatus
{
    ACK('a'), NAK_FULL('F'), NAK_INVALID('I'), NAK_STOP_CONDITION('!');

    final char state;

    EtherdreamResponseStatus(char state)
    {
        this.state = state;
    }

    public static EtherdreamResponseStatus get(char state)
    {
        return switch (state)
                {
                    case 'a' -> ACK;
                    case 'F' -> NAK_FULL;
                    case 'I' -> NAK_INVALID;
                    case '!' -> NAK_STOP_CONDITION;
                    default -> throw new IllegalStateException("Unexpected response status value value: " + state);
                };
    }
}
