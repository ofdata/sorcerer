package a.b;

/**
 * @author Kohsuke Kawaguchi
 */
public class Foo {
    Foo(int x, int y) {}
    Foo(Foo z) {}
    int x;
    Foo y;
    Foo test(Foo f) { return f; }
}
