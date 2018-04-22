# -*- coding: utf-8 -*-
"""
Created on Sat Apr 21 21:16:04 2018

@author: kelly
"""

"""
Decrypt will be the inverse of encrypt
this program references CECS378 lab assignments and cryptography.io
"""
import os
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms,modes
#from cryptography.hazmat.primitives.ciphers.modes import CBC
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import hashes,serialization

def myDecrypt(C, key, IV):
    """
   what is backend in this case???
    """
    backend = default_backend()   
    
    #creates an instance defining how we will decrypt    
    cipher = Cipher(algorithms.AES(key). modes.CBC(IV), backend=backend())
    
    #creates an decryption instance
    decryptor = cipher.decryptor()
    
    #initialize unpadder instance with 128 representing the number bytes required to make final block of data the same size as block size
    unpadder = padding.PKCS7(128).unpadder()
    
    #update and finalize decrypted cipher C
    plaintext = decryptor.update(C) + decryptor.finalize()
    
    #update and finalize unpadded data
    plaintext = unpadder.update(plaintext) + unpadder.finalize()
    
    
def myFileDecrypt(filepath):
    #split filename and extension as separate variables
    filename, ext = os.path.splitext(filepath)
    
    #open file and read as byte but read returns as a string object
    f = open(filepath, 'rb')
    message = f.read()

    #close file so it can no longer be read or written to
    f.close()
    
    #extract data from JSON
    
    plaintext = myRSADecrypt(RSACipher, C, IV, ext, RSA_privatekey_filepath)

    #create file
    
def myRSADecrypt(RSACipher, C, IV, ext, RSA_privatekey_filepath):
    #load pem private key from RSA_privatekey_filepath
    f = open(RSA_privatekey_filepath, 'rb')
    
    #create public_key from private_key with serialization
    #serialization dumps key object to bytes
    private_key = serialization.load_pem_private_key(
        f.read(),
        backend=default_backend()    
    )  
    
    #close file so it can no longer be read or written to
    f.close()
    
    key = private_key.decrypt(
        RSACipher,
        padding.OAEP(
            mfg=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None        
        )
    )
    
    plaintext = myDecrypt(C, key, IV)
    return plaintext