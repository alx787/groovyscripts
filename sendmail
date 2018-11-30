
// процедура отсылки сообщения 
// Create an email
def sendEmail(String emailAddr, String subject, String body) {
    SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
    if (mailServer) {
        Email email = new Email(emailAddr)
        email.setSubject(subject)
        email.setBody(body)
        mailServer.send(email)
        log.debug("Mail sent")
    } else {
        log.warn("Please make sure that a valid mailServer is configured")
    }
}

