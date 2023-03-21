import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parameter {
    public static double MAX = Double.MAX_VALUE;                //表示一个未知的极大值
    public static double MIN = Double.MIN_VALUE;
    public static int NodeNumber = 100;                         //表示用户需求点数量(25,50,100)
    public static int VehicleNumber = 25;                      //表示提供运力的车辆数
    public static double Capacity = 200;                        //表示每辆车的承载量
    public static int MaxIteration = 400;                      //最大迭代次数
    public static int Remove_NodeNumber = 2;
    public static String type;
    public static String dataFileName = "";
    public static double Alpha = 1000, Beta = 1000;                       //变化参数
    //public static int RouteNumber;                              //判断当Solution中的线路数量
    public static double Best_Value;                            //全局最优Cost
    public static double Local_Value;                           //当前最优Cost
    public static double New_Value;                             //经过破坏-修复后新解得Cost
    public static Node[] N = new Node[NodeNumber + 5];          //创建的节点集合
    public static Node[] Remove_Node = new Node[Remove_NodeNumber];            //每次拿掉10个节点
    public static Route[] R = new Route[NodeNumber + 5];       //当前解
    public static Route[] new_Route = new Route[NodeNumber + 5];//新解
    public static Route[] RegretNode = new Route[1];            //去除节点的集合
    public static Route[] Best_Route = new Route[NodeNumber + 5];
    public static double[][] DisMatrix = new double[NodeNumber + 5][NodeNumber + 5];
    public static double[][] RelatedMatrix = new double[NodeNumber + 5][NodeNumber + 5];//定义节点相关性矩阵
    public static double[] WDestory = new double[3];                  //破坏算子的初始权重，当前有两个破坏算子
    public static double[] WRepair = new double[3];                   //修复算子的初始权重
    public static int[] DestoryUseTime = new int[3];            //破坏算子和修复算子的使用次数
    public static int[] RepairUseTime = new int[3];             //修复算子和修复算子的使用次数
    public static int Destory_Selection;                        //破坏算子选择编号
    public static int Repair_Selection;                         //修复算子选择编号
    public static double Cost(Route[] R) {
        double Q = 0,T=0,D = 0,z = 0;
        //计算超过容量限制的总量
        for(int i = 1;i <= VehicleNumber;i++){
            if(R[i].Load>Capacity){
                Q +=(R[i].Load-Capacity);
            }
        }
        //计算超过时间窗的总量
        for(int i = 1;i <= VehicleNumber;i++){
            T +=R[i].SubT;
        }
        //计算路线长度总量
        for(int i = 1;i <= VehicleNumber;i++){
            D+=R[i].Dis;
        }
        //计算目标函数
        z = D+Parameter.Alpha*Q+Parameter.Beta*T;
        return (z);
    }

}
