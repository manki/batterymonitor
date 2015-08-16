package in.manki.batterymonitor;

/**
 * Created by mkannan on 8/15/15.
 */
final class BatteryStatus {
    private final ChargeState chargeState;
    private final int chargePercentage;

    public BatteryStatus(ChargeState chargeState, int chargePercentage) {
        this.chargeState = chargeState;
        this.chargePercentage = chargePercentage;
    }

    public ChargeState getChargeState() {
        return chargeState;
    }

    public int getChargePercentage() {
        return chargePercentage;
    }

    @Override
    public String toString() {
        return String.format("state=%s percent=%s%%", chargeState, chargePercentage);
    }
}
