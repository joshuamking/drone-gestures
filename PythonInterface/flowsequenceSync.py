# -*- coding: utf-8 -*-
#
#     ||          ____  _ __
#  +------+      / __ )(_) /_______________ _____  ___
#  | 0xBC |     / __  / / __/ ___/ ___/ __ `/_  / / _ \
#  +------+    / /_/ / / /_/ /__/ /  / /_/ / / /_/  __/
#   ||  ||    /_____/_/\__/\___/_/   \__,_/ /___/\___/
#
#  Copyright (C) 2016 Bitcraze AB
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
Simple example that connects to the crazyflie at `URI` and runs a figure 8
sequence. This script requires some kind of location system, it has been
tested with (and designed for) the flow deck.

Change the URI variable to your Crazyflie configuration.
"""
import logging
import time

import cflib.crtp
from cflib.crazyflie import Crazyflie
from cflib.crazyflie.syncCrazyflie import SyncCrazyflie

URI = 'radio://0/80/1M'

# Only output errors from the logging framework
logging.basicConfig(level=logging.ERROR)


if __name__ == '__main__':
    # Initialize the low-level drivers (don't list the debug drivers)
    cflib.crtp.init_drivers(enable_debug_driver=False)

    with SyncCrazyflie(URI, cf=Crazyflie(rw_cache='./cache')) as scf:
        cf = scf.cf

        # self._cf.commander.send_setpoint(pitch?, roll?, yawrate, thrust)

        # cf.commander.send_hover_setpoint(0.06, 0.06, 0, 0.3)
        # cf.commander.send_hover_setpoint(0, 0, 0, 0.3)
        # time.sleep(0.5)
        # cf.commander.send_hover_setpoint(0, 0, 0, 0.3)
        # time.sleep(0.5)
        # cf.commander.send_hover_setpoint(0, 0, 0, 2)
        # time.sleep(5)


        # cf.commander.send_setpoint(0,0,0, 37500)


        # time.sleep(1)
        # cf.param.set_value('kalman.resetEstimation', '1')
        # time.sleep(0.1)
        # cf.param.set_value('kalman.resetEstimation', '0')
        # time.sleep(0.2)



        delay = 0.05


        for y in range(10):
            cf.commander.send_hover_setpoint(0, 0, 0, y/25)
            time.sleep(0.1)

        # for y in range(3):
            # cf.commander.send_hover_setpoint(0, 0, 0, 1)
            # time.sleep(delay)

        # for y in range(100):
            # cf.commander.send_hover_setpoint(0, 0, 0, 0.4 * ( ((100-y)/100)))
            # time.sleep(0.05)

        for _ in range(20):
            cf.commander.send_hover_setpoint(0, 0, 0, 0.4)
            time.sleep(delay)

        for _ in range(50):
            cf.commander.send_hover_setpoint(0.5, 0, 36 * 2, 0.4)
            time.sleep(delay)

        for _ in range(50):
            cf.commander.send_hover_setpoint(0.5, 0, -36 * 2, 0.4)
            time.sleep(delay)

        for _ in range(20):
            cf.commander.send_hover_setpoint(0, 0, 0, 0.4)
            time.sleep(delay)

        for y in range(10):
            cf.commander.send_hover_setpoint(0, 0, 0, (10 - y) / 25)
            time.sleep(delay)

        cf.commander.send_stop_setpoint()
