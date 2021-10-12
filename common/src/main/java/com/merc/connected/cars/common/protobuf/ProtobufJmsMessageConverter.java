package com.merc.connected.cars.common.protobuf;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import com.google.protobuf.InvalidProtocolBufferException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ProtobufJmsMessageConverter implements MessageConverter {

    @Autowired
    private AESEncryptionManager encryptionManager;

    @Value("${spring.protobuf.encryption.key.password}")
    private String encryptionKeyPass;

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {

        BytesMessage bytesMessage = session.createBytesMessage();
        DriverProtos.Person person = (DriverProtos.Person) object;
        try {
            bytesMessage.writeBytes(encryptionManager.encryptData(encryptionKeyPass,person.toByteArray()));
        } catch (NoSuchPaddingException |
        NoSuchAlgorithmException  |
                InvalidAlgorithmParameterException   |
                InvalidKeyException  |
                BadPaddingException  |
                IllegalBlockSizeException |
                InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return bytesMessage;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        System.out.println("Converting from GoogleProtoBuf");

        BytesMessage bytesMessage = (BytesMessage) message;
        int messageLength = (int) bytesMessage.getBodyLength();
        DriverProtos.Person person = null;
        try {
            byte[] rawPerson = new byte[messageLength];
            bytesMessage.readBytes(rawPerson);
            person = DriverProtos.Person.parseFrom(encryptionManager.decryptData(encryptionKeyPass,rawPerson));
        } catch (NoSuchPaddingException |
                NoSuchAlgorithmException  |
                InvalidAlgorithmParameterException   |
                InvalidKeyException  |
                BadPaddingException  |
                IllegalBlockSizeException |
                InvalidKeySpecException |
                InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return person;
    }

}
