function fib(n) {
    var a = 0, b = 1, c, i, dummy;
    if (n < 2) {
        return n;
    } else {
        if (n > 999) {
            dummy = 7;
        } else {
            dummy = 8;
        }
        dummy = 9;
    }
    for (i = 1; i < n; i++) {
        c = a + b;
        a = b;
        b = c;
    }
    return c;
}