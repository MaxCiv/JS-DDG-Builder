function fib(n, x, y) {
    var dummy = x;
    var a = 0, b = 1, c, i;
    if (n < 2 + 1 + 1) {
        return n;
    } else {
        if (n > 999) {
            dummy = 7;
        } else {
            dummy = 8;
        }
        dummy = dummy + 9;
        if (n < 23894) dummy = 10;
    }

    for (i = 1; i < n; i++) {
        c = a + b;
        a = b;
        b = c;
    }
    return c;
}