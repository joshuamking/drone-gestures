class DroneConnectionStatusMessage:
    CONNECTED = "Connected"
    DISCONNECTED = "Disconnected"

    def __init__(self, status):
        self.type = "DroneConnectionStatusMessage"
        self.status = status
