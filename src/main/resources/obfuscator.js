var fs = require('fs')

//Путь к файлу исходного кода программы
var path = process.argv[2];  
//Считываем содержимое файла исходного кода
var fileContent = fs.readFileSync(path, "utf8");

//Применяем обфускацию клонированием функций
fileContent = CloneObfuscation(fileContent);
//Применяем к уже обфусцированному коду обфускацию вставкой избыточного кода
fileContent = ExtraCodeObfuscation(fileContent);
//Удаляем комментарии
fileContent = fileContent.replace(/\/\/[^]*?\r\n/g, "");
fileContent = fileContent.replace(/\/\*[^]*?\*\//g, "");
//fileContent = fileContent.replace(/\r\n/g, " ");

//Записываем результат
var resultFilename = path.replace(/.[\d\s\w]*$/, "") + "_obfuscated.cpp";
fs.writeFileSync(resultFilename, fileContent, "utf8");

//Обфускация клонированием функций
function CloneObfuscation(fileContent){
    var regexp =  new RegExp(/\r\n[\d\ \w\*\_]*\s[\d\w]*\([\d\s\w\,\*\_\(\)\[\]]*\)[(\s|\r\n)?]*\{[^]+?\}[^]*?[\s]*\@Clone/);
    var func = regexp.exec(fileContent);
    var i = 0;
    var clonesArr = [];
    while (func != null){
        console.log("Test")
        var funcName = func[0].match(/\s[\d\w]*\(/)[0].replace(/\(/, "").replace(" ", "");
        fileContent = fileContent.replace(func[0], "@tmp" + i)
        var funcClones = "";
        var srchrxp =  new RegExp(funcName);
        while (fileContent.search(srchrxp) != -1) {
            var newName = GenerateFunName();
            fileContent = fileContent.replace(srchrxp, newName);
            funcClones += func[0].replace(funcName, newName);
        }
        clonesArr.push(funcClones);
        func = regexp.exec(fileContent);
        console.log(funcName);
        ++i;
    }
    for (var j = 0; j < clonesArr.length; j++)
        fileContent = fileContent.replace("@tmp" + j, clonesArr[j]);
    console.log(fileContent)
    return fileContent;
}

//Обфускация вставкой избыточного кода
function ExtraCodeObfuscation(fileContent){
    var res = fileContent;
    var maxExtraFuncsCount = 20;
    var extraFuncsCount = GetRand(maxExtraFuncsCount);
    var extraFuncNames = [];
    var extraFuncs = "";
    for (var i = 0; i < extraFuncsCount; i++){
        var funcName = GenerateFunName();
        extraFuncNames.push(funcName);
        extraFuncs += GenerateFunction(funcName);
    }
    var regexp =  new RegExp(/\r\n[\d\ \w\*\_]*\s[\d\w]*\([\d\s\w\,\*\_\(\)\[\]]*\)[(\s|\r\n)?]*\{[^]+?\@InsertExtraCode/g);
    var func = regexp.exec(fileContent);
    var i = 0;
    while (func != null){
        //console.log(func[0]);
        var obfuscatedFunc = AddExtraCode(func[0], extraFuncNames);
        res = res.replace(func, (i == 0) ? extraFuncs + obfuscatedFunc : obfuscatedFunc);
        func = regexp.exec(fileContent);
        console.log(regexp.lastIndex);
        ++i;
    }
    return res;
}

//Вспомогательная функция для добавления избыточного кода в тело определенной функции
function AddExtraCode(func, extraFuncs){
    var res = "\r\n";
    var lines = func.split("\r\n");
    var insertedCount = 0;
    for (var i = 1; i < lines.length - 2; i++){
        lines[i] += "\r\n"
        res += lines[i];
        if (IsPlaceToInsert(lines[i], lines[i+1]))
            res += (GetRand(2) == 1) ? extraFuncs[GetRand(extraFuncs.length)] + "();\r\n" : "";
    }
    res += lines[lines.length - 2];
    return res;
}

//Вспомогательная функция проверки возможности вставки избыточного кода в данную строку кода 
function IsPlaceToInsert(line1, line2) {
    if ((line1.match(/[if|while|for|else|switch][^]*?\)[\s]*\r\n/) != null) || (line2.match(/else/) != null))
        return false;
    else 
        return true;
}
//Вспомогательная функция генерации произвольного имени функции
function GenerateFunName() {
  var nameLength = 10;
  return GenerateName(nameLength);
}
//Вспомогательная функция генерации произвольного имени переменной
function GenerateVarName() {
  var nameLength = 5;
  return GenerateName(nameLength);
}
//Вспомогательная функция генерации произвольной последовательности символов
function GenerateName(length){
  var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  var res = "";

  for (var i = 0; i < length; i++)
    res += chars.charAt(GetRand(chars.length));

  return res;
}
//Вспомогательная функция генерации случайного числа
function GetRand(max) {
    return Math.floor(Math.random() * max);
}

//Вспомогательная функция генерации тела избыточной функции
function GenerateFunction(funName) {
    var randMax = 100;
    var opers = ["+", "-", "*"];
    var res = "void " + funName + " () {\r\n"
    //Генерируем имена для трех переменных
    var v1 = GenerateVarName();
    var v2 = GenerateVarName();
    var v3 = GenerateVarName();
    //Инициализируем переменные произвольными значениями
    res += "    double " + v1 + " = " + GetRand(randMax) + ";\r\n"
    res += "    double " + v2 + " = " + GetRand(randMax) + ";\r\n"
    res += "    double " + v3 + " = " + v1 + " " + opers[GetRand(opers.length)] + " " + v2 + ";\r\n"
    res += "}\r\n"
    return res;
}

