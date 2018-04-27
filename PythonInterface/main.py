import logging
import json
from threading import Thread

import CommandHandler
import cflib

from AvailableInterfacesMessage import AvailableInterfacesMessage
from DroneController import DroneController
from utils import jsonPrint

logging.basicConfig(level=logging.ERROR)

MSG_PREFIX = 'CrazyflieDroneMsg|'


def wait_for_msg(msg_key) -> dict:
    msg_value = ""
    while len(msg_value) == 0:
        print("waiting for: " + msg_key)
        msg = input()

        if not (msg.startswith(MSG_PREFIX)):
            continue
        else:
            msg = msg[len(MSG_PREFIX):]
            # print(msg)

        msg = json.loads(msg)

        if not (msg['type'] == msg_key):
            continue

        return msg


def wait_for_command() -> dict:
    msg_value = ""
    while len(msg_value) == 0:
        msg = input()

        if not (msg.startswith(MSG_PREFIX)):
            continue
        else:
            msg = msg[len(MSG_PREFIX):]

        return json.loads(msg)


# def listen_for_commands():
#     print("Listening for commands")
#     while True:
#         command = wait_for_command()
#         print(command)
#         {
#             'MoveCommand': CommandHandler.move_command,
#             'HoldAltitudeCommand': CommandHandler.hold_altitude_command,
#             'FigureEightCommand': CommandHandler.figure_eight,
#             'BackCommand': CommandHandler.back,
#             'ForwardCommand': CommandHandler.forward,
#             'LandCommand': CommandHandler.land,
#             'LeftCommand': CommandHandler.left,
#             'RightCommand': CommandHandler.right,
#             'SpinCommand': CommandHandler.spin,
#             'TakeOffCommand': CommandHandler.take_off
#         }[command['type']](command, drone)
#
#     # CommandHandler.land_command(None, drone)


if __name__ == '__main__':

    print("Greetings from the Python bridge!")

    # Initialize the low-level drivers (don't list the debug drivers)
    cflib.crtp.init_drivers(enable_debug_driver=False)
    # Scan for Crazyflies and use the first one found
    availableInterfaces = []
    while len(availableInterfaces) == 0:
        print("Searching for drone...")
        availableInterfaces = cflib.crtp.scan_interfaces()

    if len(availableInterfaces) > 0:
        jsonPrint(AvailableInterfacesMessage(availableInterfaces))
        # interfaceToUse = wait_for_msg("ConnectCommand")['connectionInterface']
        interfaceToUse = "radio://0/80/1M"
        print("Using interface: " + interfaceToUse)
        drone = DroneController(interfaceToUse)
        while True:
            pass
        # Thread(target=listen_for_commands).start()
        # listen_for_commands()
        # drone.mc.stop()
