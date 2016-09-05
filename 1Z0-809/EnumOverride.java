enum Furniture {
  CHAIR,
  TABLE { void about() { System.out.println("without legs"); }  };

  void about() {
    System.out.println("comfortable");
  }
}

class EnumOverride {
  public static void main(String args[]) {
    Furniture.CHAIR.about();                 // prints "comfortable"
    Furniture.TABLE.about();                 // prints "without legs"
  }
}

