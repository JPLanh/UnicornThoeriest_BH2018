# -*- coding: utf-8 -*-
"""
Created on Sat Apr 21 16:39:51 2018

@author: kelly
"""

"""
(C,IV)=myEncrypt(messagae,key)
generate a 16 bytes IV and encrypt the message using the key and IV in CBC mode (AES)
return error if len(key)<32 (key must be 32 bytes or 256 bits)

(C,IV,key,ext)=myFileEncrypt(filepath)
generate a 32 byte key. open and read the file as a string. you then call the above method to encrypt your file using the generated key.
return C, IV, Key and extension of the file (as a string)

inverse for decrypt

(encrypt and decrypt a jpg)


RSA portion
(RSACipher, C, IV, ext)=myRSAEncrypt(filepath, RSA_publickey_filepath)
call MyfileEncrypt(filepath) which will return C, IV, key, ext. initialize an RSA public key encryption object and load pem publickey sfrom RSA_publickey_filepath.
lastly, encrypt the key variable "key" using the RSA publickey in OAEP padding mode, the result will be RSACipher
return (RSACipher, c, IV, ext) 

do inverse for myRSADecrypt(RSACipher, C, IV, ext, RSA_privatekey_filepath)

this program references CECS378 lab assignments and cryptography.io
"""
import os
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms
#from cryptography.hazmat.primitives.ciphers.modes import CBC
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import hashes, serialization, padding

def myEncrypt(message,key):
    if len(key) < 32:
        print("error, key is not correct length")
        return 0
    else:
        """
        what is backend in this case???
        """
        backend = default_backend()    
        
        #generate a 16 bytes IV
        #urandom is used over random because it cannot be seeded and draws entropy from many unpredictable sources, making it more random (stackoverflow)
        IV = os.urandom(16)
        
        #creates an instance defining how we will encrypt    
        cipher = Cipher(algorithms.AES(key). modes.CBC(IV), backend=backend())
        
        #initialize padder instance with 128 representing the number bytes required to make final block of data the same size as block size
        padder = padding.PKCS7(128).padder()
        
        #pad the message
        padded_data = padder.update(message)
        
        #concatenate and finalize the data
        padded_data += padder.finalize()
        
        #creates an encryption instance
        encryptor = cipher.encryptor()
        
        #encrypts our message with padded content
        C = encryptor.update(message) + padder.finalize()
        
        return C, IV

    
def myFileEncrypt(filepath):
    #generate a 32 byte key
    #use urandom which offers more entropy than random because it cannot be seeded
    key = os.urandom(32)
    
    #split filename and extension as separate variables
    filename, ext = os.path.splitext(filepath)
    
    #open file and read as byte but read returns as a string object
    f = open(filepath, 'rb')
    message = f.read()

    #close file so it can no longer be read or written to
    f.close()
    
    C, IV = myEncrypt(message, key)
        
    return C, IV, key, ext
    
def myRSAEncrypt(filepath, RSA_publickey_filepath):
    C, IV, key, ext = myFileEncrypt(filepath)
    
    """
    should private key generation go in main?
    """
    #create private key using prime number 65537 and 2048 bits in length
    private_key = rsa.generate_private_key(
        public_exponent = 65537,
        key_size = 2048,
        backend = default_backend()
    )
    
    #load pem public key from RSA_publickey_filepath
    f = open(RSA_publickey_filepath, 'rb')
    
    #create public_key from private_key with serialization
    #serialization dumps key object to bytes
    public_key = serialization.load_pem_public_key(
        f.read(),
        backend=default_backend()    
    )  
    
    #close file so it can no longer be read or written to
    f.close()
    
    #encrypt pemKey using our RSA public key
    #using OAEP padding and SHA256
    RSACipher = public_key.encrypt(
        key,
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )
    
    return RSACipher, C, IV, ext

