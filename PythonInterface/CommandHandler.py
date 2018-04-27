import time

from DroneController import DroneController


def move_command(command: dict, drone: DroneController):
    roll = float(command['roll'])
    thrust = float(command['thrust'])
    pitch = float(command['pitch'])
    yaw = float(command['yaw'])

    print("thrust", thrust, "roll:", roll, "pitch:", pitch, "yaw:", yaw)

    min(thrust, 2)
    min(roll, 64000)
    min(pitch, 64000)
    min(yaw, 64000)

    if thrust < 0:
        thrust = 0

    print("thrust", thrust, "roll:", roll, "pitch:", pitch, "yaw:", yaw)

    drone.set_roll(roll)
    drone.set_thrust(thrust)
    drone.set_pitch(pitch)
    drone.set_yaw(yaw)


def hold_altitude_command(command: dict, drone: DroneController):
    drone.should_hold_altitude = command['shouldHoldAltitude']


def land(command: dict, drone: DroneController):
    print("landing")
    drone.mc.land()


def take_off(command: dict, drone: DroneController):
    drone.reset_kalman()
    drone.mc.take_off()


def figure_eight(command: dict, drone: DroneController):
    print("Starting Figure Eight sequence!")

    loop_size = 0.2

    drone.mc.turn_right(45)
    time.sleep(0.3)
    drone.mc.move_distance(loop_size, 0, 0, velocity=0.5)
    drone.mc.circle_left(loop_size, velocity=0.5, angle_degrees=270)
    drone.mc.move_distance(loop_size * 2, 0, 0, velocity=0.5)
    drone.mc.circle_right(loop_size, velocity=0.5, angle_degrees=270)
    drone.mc.move_distance(loop_size, 0, 0, velocity=0.5)
    time.sleep(0.3)
    drone.mc.turn_left(45)


def stop(command: dict, drone: DroneController):
    drone._cf.commander.send_stop_setpoint()


movement_distance, movement_velocity = 0.15, 0.5


def back(command: dict, drone: DroneController):
    drone.mc.start_back(movement_velocity)


def forward(command: dict, drone: DroneController):
    drone.mc.start_forward(movement_velocity)


def halt(command: dict, drone: DroneController):
    drone.mc.stop()


def right(command: dict, drone: DroneController):
    drone.mc.start_right(movement_velocity)


def left(command: dict, drone: DroneController):
    drone.mc.start_left(movement_velocity)


def spin(command: dict, drone: DroneController):
    drone.mc.start_turn_left()


def change_altitude(command: dict, drone: DroneController):
    altitude = float(command['altitude'])

    if altitude > 0:
        drone.mc.start_up()
    elif altitude < 0:
        drone.mc.start_down()
    else:
        drone.mc.stop()
