package team.lodestar.lodestone.modules.toolkit.creative_tab.slot;

public class SlotLocation {

    protected int row;
    protected int column;
    protected int index;

    public void step() {
        index++;
        column++;
        if (column == 9) {
            column = 0;
            row++;
        }
    }

    public void nextLine(boolean force) {
        int missing = 9 - column;
        if (!force && missing == 9) {
            return;
        }
        index += missing;
        column = 0;
        row++;

    }

    public int getIndex() {
        return index;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
