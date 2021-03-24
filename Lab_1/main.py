import socket
from threading import *
import datetime
import json
import os
import time

USER_NAME = input('### YOUR NAME: ')
HOST = socket.gethostbyname(socket.gethostname())
print('### YOUR HOST: ', HOST)
PORT = int(input('### YOUR PORT: '))
CONNECT_HOST = input('### CONNECT HOST: ')
CONNECT_PORT = int(input('### CONNECT PORT: '))

unchecked_messages = []
all_messages = []


def sender(server):
    last_id = 0
    while True:
        message_dict = {
            'type': 'message',
            'author': USER_NAME,
            'text': input(),
            'time': str(datetime.datetime.now()),
            'id': last_id,
        }
        last_id += 1
        message = json.dumps(message_dict)
        server.sendto(message.encode(), (CONNECT_HOST, CONNECT_PORT))
        unchecked_messages.append(message_dict)
        all_messages.append(message_dict)
        console_update()


def receiver(server):
    resending = False
    while True:
        raw_msg, _ = server.recvfrom(2048)
        message = json.loads(raw_msg.decode('utf-8'))
        if message['type'] == 'message':
            all_messages.append(message)
            console_update()
            response = {
                'type': 'response',
                'id': message['id'],
            }
            server.sendto(json.dumps(response).encode(), (CONNECT_HOST, CONNECT_PORT))
        elif message['type'] == 'response':
            global unchecked_messages
            unchecked_messages = list(filter(lambda x: x['id'] != message['id'], unchecked_messages))
            if len(unchecked_messages) > 0:
                if not resending:
                    print('### MESSAGES WERE LOST')
                    resend_thread = Thread(target=resend, args=[server])
                    resend_thread.start()
                    resending = True
            else:
                resending = False


def resend(server):
    global unchecked_messages
    attempts = 10
    while len(unchecked_messages) > 0 and attempts > 0:
        print('### RESENDING. REMAINING ATTEMPTS = ', attempts)
        for msg in unchecked_messages:
            server.sendto(json.dumps(msg).encode(), (CONNECT_HOST, CONNECT_PORT))
        time.sleep(5)
        attempts -= 1
    else:
        if attempts == 0:
            print('### CONNECTION LOST')
        elif len(unchecked_messages) == 0:
            print('### ALL MESSAGES HAVE BEEN RESENT')


def console_update():
    os.system('clear')

    def custom_key(o):
        return o['time']

    all_messages.sort(key=custom_key)
    for message in all_messages:
        print('{} by {}: {}'.format(message['time'], message['author'], message['text']))


def main():
    os.system('clear')
    server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server.bind((HOST, PORT))
    s_thread = Thread(target=sender, args=[server])
    r_thread = Thread(target=receiver, args=[server])

    s_thread.start()
    r_thread.start()


if __name__ == '__main__':
    main()
