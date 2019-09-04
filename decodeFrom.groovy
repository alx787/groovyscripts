String testString = "Наладить доступ к цветному принтеру директора из приемной. (Ошибка: принтер не подключен). (давняя проблема) [Created via e-mail received from: =?koi8-r?B?xxxxxxxx=?= <xxx@xxx.ru>]"

// реверсим строку
String reversString = testString.reverse()

//
int closeSq = reversString.indexOf("]")
int openSq = reversString.indexOf("[")
int lenString = testString.length()

String mailFromText = testString.substring(lenString - openSq - 1, lenString - closeSq)


///////////////////////////
String mailFromAlias = mailFromText
mailFromAlias = mailFromAlias.substring(mailFromAlias.indexOf("=?"));
mailFromAlias = mailFromAlias.substring(0, mailFromAlias.indexOf("?=") + 2);

javax.mail.internet.MimeUtility.decodeText(mailFromAlias)



///////////////////////////
String mailFromAddress = mailFromText
mailFromAddress = mailFromAddress.substring(mailFromAddress.indexOf("<") + 1);
mailFromAddress = mailFromAddress.substring(0, mailFromAddress.indexOf(">"));


