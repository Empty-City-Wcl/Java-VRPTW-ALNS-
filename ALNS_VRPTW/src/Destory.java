import java.util.Random;

public class Destory {                 //破坏算子类

    public static void Random_Destory(Route[] R) {           //随机破坏0

        int[] NodeIDIndex = new int[Parameter.Remove_NodeNumber];
        int a = 0;
        //t b = 0;
        while ( a < Parameter.Remove_NodeNumber) {
            int b = Calculation.IntRandom(2,Parameter.NodeNumber+2);
            if (useLoop(NodeIDIndex,b) == false) {
                NodeIDIndex[a] = b;
                a++;
            }
        }


        //找到随机编号节点的具体位置
        //int RouteIndex;
        for (int i = 0;i < NodeIDIndex.length;i++) {
            //RouteIndex = Parameter.N[NodeIDIndex[i]].R;
            for (int RouteIndex = 1;RouteIndex <= Parameter.VehicleNumber;RouteIndex++) {
                for (int j = 1; j < R[RouteIndex].N.size(); j++) {
                    if (NodeIDIndex[i] == R[RouteIndex].N.get(j).ID) {
                        Calculation.RemoveNode(R, RouteIndex, j, NodeIDIndex[i]);
                        break;
                    }
                }
            }
        }
        for (int i = 0;i < NodeIDIndex.length;i++) {
            //Parameter.N[NodeIDIndex[i]].R = 0;      //更新线路
            Parameter.Remove_Node[i] = new Node(Parameter.N[NodeIDIndex[i]]);   //放入移除表中
        }
    }

    public static void Greedy_Destory(Route[] R) {          //贪婪破坏1
        double Cost_Old;
        double Cost_New;
        int RouteIndex = 0;
        int PositionIndex = 0;
        int NodeID1 = 0;
        int NodeID2 = 0;

        for (int m = 0;m < Parameter.Remove_NodeNumber;m++) {
            double Value = Parameter.MIN;
            Cost_Old = Parameter.Cost(R);                                    //计算当前Cost
            for (int i = 1; i <= Parameter.VehicleNumber+1; i++) {              //遍历每一条线路
                for (int j = 1; j <= R[i].N.size() - 2; j++) {              //遍历当前路径的每一个节点
                    NodeID1 = R[i].N.get(j).ID;                             //记录当前节点的ID
                    Calculation.RemoveNode(R, i, j, NodeID1);               //移除当前节点
                    Cost_New = Parameter.Cost(R);                           //计算新的Cost

                    if ((Cost_Old - Cost_New) > Value) {                   //若SuBT减少的更多
                        Value = Cost_Old - Cost_New;                        //则更新Value
                        RouteIndex = i;
                        PositionIndex = j;
                        NodeID2 = NodeID1;                                  //并记更优节点相关信息
                    }
                    Calculation.AddNode(R, i, j, NodeID1);                  //将节点插回
                }
            }
            Parameter.N[NodeID2].R = 0;      //移除所属线路
            Parameter.Remove_Node[m] = new Node(Parameter.N[NodeID2]);      //将最优去除节点加入破坏节点表中
            Calculation.RemoveNode(R, RouteIndex, PositionIndex, NodeID2);     //在线路中移除选定节点
        }
    }

    public static void Shaw_Destory(Route[] R) {           //相关性破坏2

        int[] NodeIDIndex = new int[Parameter.Remove_NodeNumber];
        int a = Calculation.IntRandom(2,Parameter.NodeNumber+2);        //先随机产生一个节点ID编号
        NodeIDIndex[0] = a;                                                 //将随机节点ID放入IndexID中

        for (int m = 1;m < Parameter.Remove_NodeNumber;m++) {
            double Value = Parameter.MIN;
            int RelatedID = 0;
            for (int i = 2; i <= Parameter.NodeNumber + 1; i++) {
                if (Parameter.RelatedMatrix[NodeIDIndex[m-1]][i] > Value) {        //若找到更大的Related值，则记录节点id
                    Value = Parameter.RelatedMatrix[NodeIDIndex[m-1]][i];
                    RelatedID = i;
                }
            }
            NodeIDIndex[m] = RelatedID;
        }

        //找到随机编号节点的具体位置
        for (int i = 0;i < NodeIDIndex.length;i++) {
            for (int RouteIndex = 1;RouteIndex <= Parameter.VehicleNumber;RouteIndex++) {
                for (int j = 1; j < R[RouteIndex].N.size(); j++) {
                    if (NodeIDIndex[i] == R[RouteIndex].N.get(j).ID) {
                        Calculation.RemoveNode(R, RouteIndex, j, NodeIDIndex[i]);
                        break;
                    }
                }
            }
        }
        for (int i = 0;i < NodeIDIndex.length;i++) {
            Parameter.Remove_Node[i] = new Node(Parameter.N[NodeIDIndex[i]]);   //放入移除表中
        }
    }


    public static Boolean useLoop(int[] arr, int Value) {           //判断有无重复
        for (int s : arr) {
            if (s == Value)
                return true;
        }
        return false;
    }
}
