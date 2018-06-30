import Crypto.Random
from Crypto.Cipher import AES
import hashlib
import base64

SALT_SIZE = 16
AES_MULTIPLE = 16
NUMBER_OF_ITERATIONS = 20

def generate_key(password, salt):
    key = password + salt
    for i in range(NUMBER_OF_ITERATIONS):
        key = hashlib.sha256(key).digest()
    return key

def pad_text(text, multiple):
    extra_bytes = len(text) % multiple
    padding_size = multiple - extra_bytes
    padding = chr(padding_size) * padding_size
    padded_text = text + padding
    return padded_text

def unpad_text(padded_text):
    padding_size = ord(padded_text[-1])
    text = padded_text[:-padding_size]
    return text

def encrypt(plaintext, password):
    salt = Crypto.Random.get_random_bytes(SALT_SIZE)
    key = generate_key(password, salt)
    cipher = AES.new(key, AES.MODE_ECB)
    padded_plaintext = pad_text(plaintext, AES_MULTIPLE)
    ciphertext = cipher.encrypt(padded_plaintext)
    ciphertext_with_salt = salt + ciphertext
    return base64.b64encode(ciphertext_with_salt)

def decrypt(ciphertext, password):
    ciphertext = base64.b64decode(ciphertext)
    salt = ciphertext[0:SALT_SIZE]
    ciphertext_sans_salt = ciphertext[SALT_SIZE:]
    key = generate_key(password, salt)
    cipher = AES.new(key, AES.MODE_ECB)
    padded_plaintext = cipher.decrypt(ciphertext_sans_salt)
    plaintext = unpad_text(padded_plaintext)
    return plaintext
