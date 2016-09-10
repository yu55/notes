class GenericWrapper<T> {
  public void setMessage(T message) {
  }
}

class SpecialWrapper<S extends CharSequence> extends GenericWrapper<String> {
  public void setMessage(S message) {
  }
}

class ExtendsGenericClassTest {
  public static void main(String[] args) {
    // Below is OK
    new SpecialWrapper<StringBuilder>().setMessage(new StringBuilder("Hello"));

    // Below will fail because of ambigous setMessage() methods.
    // The  setMessage() methods are overloaded, not overriden (because of unknown S type at compilation time)
    new SpecialWrapper<String>().setMessage("Hello");
  }
}

