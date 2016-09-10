class NoTerminalOperation {
  public static void main(String args[]) {
    // won't print anything because no terminal operation so stream is not evaluated at all
    "abracadabra".chars().distinct().peek(ch -> System.out.printf("%c ", ch)).sorted();

    // prints "a b r c d"
    "abracadabra".chars().distinct().peek(ch -> System.out.printf("%c ", ch)).sorted().count();
  }
}

