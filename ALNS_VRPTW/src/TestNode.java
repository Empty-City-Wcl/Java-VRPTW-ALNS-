public class TestNode {
    public static int TestNodeNumber(Route[] R) {
        int NodeNumber = 0;
        for (int i = 1;i <= Parameter.VehicleNumber;i++) {
            if (R[i].N.size() > 2) {
                NodeNumber += R[i].N.size() - 2;
            }
        }
        return NodeNumber;
    }

    public static boolean TestNode1(Route[] R, int NodeID1, int NodeID2) {
        for (int i = 1;i <= Parameter.VehicleNumber;i++) {
            for (int j = 1;j < R[i].N.size();j++) {
                if (R[i].N.get(j).ID == NodeID1 || R[i].N.get(j).ID == NodeID2) {
                    return true;
                }
            }
        }
        return false;
    }
}
