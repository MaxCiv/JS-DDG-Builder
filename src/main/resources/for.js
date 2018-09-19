var d = new Date();
var time = d.getHours();

if (time < 10) {
    document.write("<b>Доброе утро</b>");
}
else {
    document.write("<b>Добрый день</b>");
}

var i = 0;
for (i = 0; i <= 5; i++) {
    document.write("Число i равно " + i);
    document.write("<br />");
}

function fib(n) {
    var a = 0, b = 1, c, i;
    if (n < 2)
        return n;
    for (i = 1; i < n; i++) {
        c = a + b;
        a = b;
        b = c;
    }
    return c;
}