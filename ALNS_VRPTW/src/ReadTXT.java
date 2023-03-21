import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class ReadTXT {
    public static void importFileInfo(String name, String Filetype) {
        Parameter.type = Filetype;
        //创建初始节点、线路表
        if (Parameter.type.equals("solomon")) {
            Parameter.dataFileName = "./instances" + "/solomon" + "/solomon_" + Parameter.NodeNumber + "/" + name + ".txt";
        } else if (Parameter.type.equals("homberger")) {
            Parameter.dataFileName = "./instances" + "/homberger" + "/homberger_" + Parameter.NodeNumber + "/" + name + ".txt";
        }
    }

    public static void InitNodeandRoute() {
        for (int i = 0;i < Parameter.NodeNumber + 5;i++) {
            Parameter.N[i] = new Node();
            Parameter.R[i] = new Route();
            Parameter.Best_Route[i] = new Route();
            Parameter.new_Route[i] = new Route();
        }
        Parameter.RegretNode[0] = new Route();          //初始化后悔插入中需要用到的列表
    }

    public static void importInfo() throws IOException {
        //写入车辆参数
        BufferedReader VehicleReader = new BufferedReader(new FileReader(Parameter.dataFileName));
        int Row = 0;
        String Line;
        while ((Line = VehicleReader.readLine()) != null) {           //若所读取Line中的信息不为空
            String DataValue[] = Line.split("\\s+");

            if (Row == 4) {
                //读取车辆数量
                Parameter.VehicleNumber = Integer.valueOf(DataValue[1]);
                //读取车辆最大容量
                Parameter.Capacity = Integer.valueOf(DataValue[2]);
                break;
            }
            Row++;
        }
        VehicleReader.close();

        //写入节点参数
        BufferedReader NodeReader = new BufferedReader(new FileReader(Parameter.dataFileName));
        int row = 0;
        String line;
        while ((line = NodeReader.readLine()) != null) {
            String dataValue[] = line.split("\\s+");

            if (row >=9) {
                Parameter.N[row-8].ID = Integer.valueOf(dataValue[1]) + 1;
                Parameter.N[row-8].demand = Integer.valueOf(dataValue[4]);
                Parameter.N[row-8].ReadyTime = Integer.valueOf(dataValue[5]);
                Parameter.N[row-8].Duetime = Integer.valueOf(dataValue[6]);
                Parameter.N[row-8].ServiceTime = Integer.valueOf(dataValue[7]);
                Parameter.N[row-8].x = Integer.valueOf(dataValue[2]);
                Parameter.N[row-8].y = Integer.valueOf(dataValue[3]);
            }
            if (row == Parameter.NodeNumber+9) {
                break;
            }
            row++;
        }
    }

    public static void ExtracRoutes() {             //分割线路

        //生成初始解(2-101)随机打乱
        int[] New_index = new int[Parameter.NodeNumber];
        for (int i = 0; i < Parameter.NodeNumber; ++i) {
            New_index[i] = i + 2;
        }
        for (int i = 0; i < New_index.length; i++) {

            //生成一个随机索引
            int randomIndex = Calculation.IntRandom(0,New_index.length);

            //拿着随机索引指向的元素 跟 i 指向的元素进行交换
            int temp = New_index[i];
            New_index[i] = New_index[randomIndex];
            New_index[randomIndex] = temp;
        }
//        System.out.println(New_index);
        for (int i = 1;i <= Parameter.VehicleNumber;i++) {
            if (Parameter.R[i].N.size() != 0) {
                Parameter.R[i].N.clear();
            }

            Parameter.R[i].N.add(new Node(Parameter.N[1]));
            Parameter.R[i].N.add(new Node(Parameter.N[1]));
            Parameter.R[i].N.get(0).Duetime = Parameter.N[1].ReadyTime;
            Parameter.R[i].N.get(1).ReadyTime = Parameter.N[1].Duetime;
            Parameter.R[i].Load = 0;
        }

        int Current_Route = 1;
        for (int i = 1;i <= Parameter.NodeNumber;i++) {
            int c = New_index[i-1];
            if (Parameter.R[Current_Route].Load + Parameter.N[c].demand > Parameter.Capacity) {     //检查一条线路的容量情况，若容量超出，则更换线路
                Current_Route++;
            }
            for (int j = 0;j < Parameter.R[Current_Route].N.size()-1;j++) {
                if (Parameter.R[Current_Route].N.get(j).ReadyTime <= Parameter.N[c].ReadyTime &&
                        Parameter.N[c].ReadyTime <= Parameter.R[Current_Route].N.get(j+1).ReadyTime) {
                    Parameter.N[c].R = Current_Route;
                    Parameter.R[Current_Route].N.add(j+1,new Node(Parameter.N[c]));
                    Parameter.R[Current_Route].Load = Parameter.R[Current_Route].Load + Parameter.N[c].demand;
                    break;
                }
            }
            Route.UPDis(Parameter.R[Current_Route]);            //更新路径的长度
            Route.UPSubT(Parameter.R[Current_Route]);           //更新路径中时间窗的总量
        }
    }
}
