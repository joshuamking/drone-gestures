# -*- coding: utf-8 -*-
#
#     ||          ____  _ __
#  +------+      / __ )(_) /_______________ _____  ___
#  | 0xBC |     / __  / / __/ ___/ ___/ __ `/_  / / _ \
#  +------+    / /_/ / / /_/ /__/ /  / /_/ / / /_/  __/
#   ||  ||    /_____/_/\__/\___/_/   \__,_/ /___/\___/
#
#  Copyright (C) 2017 Bitcraze AB
#
#  Crazyflie Nano Quadcopter Client
#
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 2
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#  MA  02110-1301, USA.
"""
This script shows the basic use of the MotionCommander class.

Simple example that connects to the crazyflie at `URI` and runs a
sequence. This script requires some kind of location system, it has been
tested with (and designed for) the flow deck.

Change the URI variable to your Crazyflie configuration.
"""
import logging
import time
import sys

import cflib.crtp
from cflib.crazyflie import Crazyflie
from cflib.crazyflie.syncCrazyflie import SyncCrazyflie
from cflib.positioning.motion_commander import MotionCommander
from cflib.crazyflie.log import Log, LogVariable, LogConfig

URI = 'radio://0/80/1M'

# Only output errors from the logging framework
logging.basicConfig(level=logging.INFO)


def monitor_batt(self, ident, data, logconfig):
    bat = float(data["pm.vbat"])
    if bat <= 3.4:
        logging.error("Battery is at {0:.2f} Volts".format(bat))
        sys.exit()
    elif bat <= 3.7:
        logging.warning("Battery is at {0:.2f} Volts".format(bat))
    else:
        logging.info("Battery is at {0:.2f} Volts".format(bat))


if __name__ == '__main__':
    # Initialize the low-level drivers (don't list the debug drivers)
    cflib.crtp.init_drivers(enable_debug_driver=False)

    # logBatt = LogConfig("Battery",200)
    # logBatt.add_variable("pm.vbat", "float")
    # cf.log.add_config(logBatt)
    # if logBatt.valid:
    #     logBatt.data_received_cb.add_callback(monitor_batt)
    #     logBatt.start()
    # else:
    #     logging.error("Could not setup log configuration for battery!!")

    with SyncCrazyflie(URI, cf=Crazyflie(rw_cache='./cache')) as scf:

        scf.cf.param.set_value('kalman.resetEstimation', '1')
        time.sleep(0.1)
        scf.cf.param.set_value('kalman.resetEstimation', '0')
        time.sleep(0.2)

        # We take off when the commander is created
        with MotionCommander(scf) as mc:
            time.sleep(1)

            # There is a set of functions that move a specific distance
            # We can move in all directions

            # mc.up(1)
            while True:
                # if not input() == "":
                    # break
                scf.cf.param.set_value('kalman.resetEstimation', '1')
                time.sleep(0.1)
                scf.cf.param.set_value('kalman.resetEstimation', '0')
                time.sleep(0.2)

                mc.land()
                time.sleep (1)
                mc.take_off()
                time.sleep (1)

                # mc.move_distance(1, 0, 0.5, velocity=0.25)
                # mc.turn_right(45)

                # loopSize = 0.2

                # mc.move_distance(loopSize, 0, 0, velocity=0.5)
                # mc.circle_left(loopSize, velocity=0.25, angle_degrees=270)
                # mc.move_distance(loopSize * 2, 0, 0, velocity=0.5)
                # mc.circle_right(loopSize, velocity=0.25, angle_degrees=270)
                # mc.move_distance(loopSize, 0, 0, velocity=0.5)

                # mc.turn_left(45)
                # # mc.forward(1, velocity=0.75)

                # # mc.turn_right(180)
                # # mc.turn_right(10)
                # time.sleep(0.5)

                # for val in range(100):
                #     if val <= 50:
                #         y = 0.02
                #     else:
                #         y = -0.02

                #     if val <= 25 or val >= 75:
                #         x = -0.02
                #     else:
                #         x = 0.02

                #     mc.move_distance(x, 0, y, velocity=0.25)
                # mc.move_distance(0, .5, 0.25, velocity=0.5)

            time.sleep(1)

            # mc.up(0.5)
            # mc.down(0.3)
            # mc.up(0.8)
            # mc.down(0.8)
            # time.sleep(1)
            # mc.circle_right(0.1, velocity=0.5, angle_degrees=360)
            # time.sleep(1)

            # # We can also set the velocity
            # # mc.right(0.5, velocity=0.8)
            # # time.sleep(1)
            # # mc.left(0.5, velocity=0.4)
            # # time.sleep(1)

            # # # We can do circles or parts of circles
            # # mc.circle_right(0.5, velocity=0.5, angle_degrees=180)

            # # # Or turn
            # mc.turn_left(90)
            # time.sleep(1)

            # # # We can move along a line in 3D space
            # mc.move_distance(-1, 0.0, 0.5, velocity=0.6)
            # mc.move_distance(1, 0.0, -0.5, velocity=0.6)
            # mc.move_distance(-1, 0.0, 0.5, velocity=0.6)
            # mc.move_distance(1, 0.0, -0.5, velocity=0.6)
            # time.sleep(1)

            # # There is also a set of functions that start a motion. The
            # # Crazyflie will keep on going until it gets a new command.

            # mc.start_left(velocity=0.5)
            # The motion is started and we can do other stuff, printing for
            # instance
            # for _ in range(5):
                # print('Doing other work')
                # time.sleep(0.2)

            # And we can stop
            mc.stop()

            # We land when the MotionCommander goes out of scope
