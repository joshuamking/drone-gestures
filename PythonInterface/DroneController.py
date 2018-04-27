import json
from threading import Thread

import time

import CommandHandler
from DroneConnectionStatusMessage import DroneConnectionStatusMessage
from cflib.crazyflie import Crazyflie
from cflib.crazyflie.syncCrazyflie import SyncCrazyflie
from cflib.positioning.motion_commander import MotionCommander
from utils import jsonPrint

MSG_PREFIX = 'CrazyflieDroneMsg|'


def wait_for_command() -> dict:
    msg_value = ""
    while len(msg_value) == 0:
        msg = input()

        if not (msg.startswith(MSG_PREFIX)):
            continue
        else:
            msg = msg[len(MSG_PREFIX):]

        return json.loads(msg)


class DroneController:
    """Example that connects to a Crazyflie and ramps the motors up/down and
    the disconnects"""

    def __init__(self, link_uri):
        """ Initialize and run the example with the specified link_uri """

        self.roll = 0
        self.thrust = 0
        self.pitch = 0
        self.yawrate = 0

        self.should_hold_altitude = False

        self._cf = Crazyflie(rw_cache='./cache')

        self._cf.connected.add_callback(self._connected)
        self._cf.disconnected.add_callback(self._disconnected)
        self._cf.connection_failed.add_callback(self._connection_failed)
        self._cf.connection_lost.add_callback(self._connection_lost)

        # self._cf.open_link(link_uri)

        self._scf = SyncCrazyflie(link_uri, cf=self._cf)
        self._scf.__enter__()

        # self._connected(link_uri)

        print('Connecting to %s' % link_uri)

    def _connected(self, link_uri):
        """ This callback is called form the Crazyflie API when a Crazyflie
        has been connected and the TOCs have been downloaded."""

        # Start a separate thread to do the motor test.
        # Do not hijack the calling thread!

        self.reset_kalman()
        self._test_motors()

        jsonPrint(DroneConnectionStatusMessage(DroneConnectionStatusMessage.CONNECTED))
        self.connected = True
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
        jsonPrint(DroneConnectionStatusMessage(DroneConnectionStatusMessage.DISCONNECTED))
        self.connected = False

    def _test_motors(self):
        print("Running motor test!")
        motor_names = ["m1", "m2", "m3", "m4"]
        self._cf.param.set_value("motorPowerSet.enable", "1")

        for motor in motor_names:
            self._cf.param.set_value("motorPowerSet." + motor, "8000")

        time.sleep(1)

        for motor in motor_names:
            self._cf.param.set_value("motorPowerSet." + motor, "0")

        time.sleep(0.1)

        self._cf.param.set_value("motorPowerSet.enable", "0")
        time.sleep(0.1)
        print("Motor test complete!")

    def _talk_to_drone(self):
        # # Unlock startup thrust protection
        # self._cf.commander.send_setpoint(0, 0, 0, 0)
        # # self._cf.commander.send_setpoint(0, 0, 0, 4000)
        #
        # # self._test_motors()
        #
        # while self.connected:
        #     # print(self.roll, self.pitch, self.yawrate, self.thrust)
        #     # if self.should_hold_altitude:
        #     #     self._cf.param.set_value("flightmode.althold", "1")
        #     # else:
        #     #     self._cf.param.set_value("flightmode.althold", "0")
        #     #     32767
        #     if self.thrust > 0:
        #         self._cf.commander.send_hover_setpoint(self.pitch, self.roll, self.yawrate, self.thrust)
        #
        # self._cf.commander.send_setpoint(0, 0, 0, 0)
        # # Make sure that the last packet leaves before the link is closed
        # # since the message queue is not flushed before closing
        # time.sleep(0.1)
        # # self._cf.close_link()

        with MotionCommander(self._scf) as mc:
            self.mc = mc

            while self.connected:
                try:
                    # print("Still alive..")
                    command = wait_for_command()
                    # print(command)
                    {
                        'MoveCommand': CommandHandler.move_command,
                        'HoldAltitudeCommand': CommandHandler.hold_altitude_command,
                        'FigureEightCommand': CommandHandler.figure_eight,

                        'ChangeAltitudeCommand': CommandHandler.change_altitude,
                        'HaltCommand': CommandHandler.halt,
                        'BackCommand': CommandHandler.back,
                        'ForwardCommand': CommandHandler.forward,
                        'LandCommand': CommandHandler.land,
                        'LeftCommand': CommandHandler.left,
                        'RightCommand': CommandHandler.right,
                        'SpinCommand': CommandHandler.spin,
                        'TakeOffCommand': CommandHandler.take_off
                    }[command['type']](command, self)
                except Exception:
                    pass

            mc.stop()

    def set_roll(self, roll):
        self.roll = float(roll)

    def set_thrust(self, thrust):
        self.thrust = float(thrust)

    def set_pitch(self, pitch):
        self.pitch = float(pitch)

    def set_yaw(self, yaw):
        self.yawrate = yaw

    def reset_kalman(self):
        self._cf.param.set_value('kalman.resetEstimation', '1')
        time.sleep(0.1)
        self._cf.param.set_value('kalman.resetEstimation', '0')
        time.sleep(0.1)
