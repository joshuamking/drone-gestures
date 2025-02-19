import logging
import time
from threading import Thread

import cflib
from cflib.crazyflie import Crazyflie

logging.basicConfig(level=logging.ERROR)

MSG_PREFIX='CrazyflyDroneMsg'

class MotorRampExample:
    """Example that connects to a Crazyflie and ramps the motors up/down and
    the disconnects"""

    def __init__(self, link_uri):
        """ Initialize and run the example with the specified link_uri """

        self._cf = Crazyflie(rw_cache='./cache')

        self._cf.connected.add_callback(self._connected)
        self._cf.disconnected.add_callback(self._disconnected)
        self._cf.connection_failed.add_callback(self._connection_failed)
        self._cf.connection_lost.add_callback(self._connection_lost)

        self._cf.open_link(link_uri)

        print('Connecting to %s' % link_uri)

    def _connected(self, link_uri):
        """ This callback is called form the Crazyflie API when a Crazyflie
        has been connected and the TOCs have been downloaded."""

        # Start a separate thread to do the motor test.
        # Do not hijack the calling thread!
        Thread(target=self._talk_to_drone).start()

    def _connection_failed(self, link_uri, msg):
        """Callback when connection initial connection fails (i.e no Crazyflie
        at the specified address)"""
        print('Connection to %s failed: %s' % (link_uri, msg))

    def _connection_lost(self, link_uri, msg):
        """Callback when disconnected after a connection has been made (i.e
        Crazyflie moves out of range)"""
        print('Connection to %s lost: %s' % (link_uri, msg))

    def _disconnected(self, link_uri):
        """Callback when the Crazyflie is disconnected (called in all cases)"""
        print('Disconnected from %s' % link_uri)

    def _talk_to_drone(self):
        thrust_mult = 1
        thrust_step = 100
        thrust = 1000
        pitch = 0
        roll = 0
        yawrate = 0

        # Unlock startup thrust protection
        self._cf.commander.send_setpoint(0, 0, 0, 0)
        # self._cf.commander.send_setpoint(0, 0, 0, 4000)
        while thrust >= 1000:
            self._cf.commander.send_setpoint(roll, pitch, yawrate, thrust)
            time.sleep(0.1)
            if thrust >= 4500:
                thrust_mult = -1
            thrust += thrust_step * thrust_mult
        self._cf.commander.send_setpoint(0, 0, 0, 0)
        # Make sure that the last packet leaves before the link is closed
        # since the message queue is not flushed before closing
        time.sleep(0.1)
        self._cf.close_link()

def getMsg(msgKey):
    msgValue = ""
    while len(msgValue) == 0:
        msg = input()
        if not(msg.)
        msg[len(MSG_PREFIX)+1:]



if __name__ == '__main__':
    # Initialize the low-level drivers (don't list the debug drivers)
    cflib.crtp.init_drivers(enable_debug_driver=False)
    # Scan for Crazyflies and use the first one found
    # print('Scanning interfaces for Crazyflies...')
    available = cflib.crtp.scan_interfaces()
    availableInterfaces = "";
    for i in available:
        # print(i)
        availableInterfaces = availableInterfaces + i[0] + "|"
    availableInterfaces = availableInterfaces[0:-1]
    print('CrazyflyDroneMsg:availableInterfaces:'+availableInterfaces)

    interfaceToUse = getMsg("useInterface")

    if len(available) > 0:
        le = MotorRampExample(interfaceToUse)
    else:
        print('CrazyflyDroneMsg:error:No Crazyflies found')
