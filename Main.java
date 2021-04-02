import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
public class Main {
    static boolean contains(String device_id, double temp, ArrayList<Termostat> t)
    {
        // check if the element already exists in an array list
        int i;
        boolean res=false;
        for(i=0;i<t.size();i++)
        {
            if( (t.get(i).device_id.equals(device_id)) && t.get(i).temp == temp)
            {
                res=true;
            }
        }
        return res;
    }

    static ArrayList<ArrayList<Termostat>> Observe(int time_reference,int time,String device_id,Double temp, ArrayList<ArrayList<Termostat>> t,ArrayList<Room>rooms)
    {
        // add object Termostat on a different line of a 2-dimensional Array List depending on a difference between times
        int i;
        if((time_reference-time)<86400)
        {
            Termostat term=new Termostat(device_id,time,temp);
            // create a new object term
            for(i=0;i<24;i++) {
                if (((time_reference - time) > ((i) * 3600)) && (time_reference - time) <= ((i+1) * 3600))
                {
                    if (contains(term.device_id, term.temp, t.get(i)))
                    {
                        return t;
                    }
                    t.get(i).add(term);
                    t.get(i).sort(new TermSorter());
                }
            }
        }
        return t;
    }

    static void List(String room, ArrayList<ArrayList<Termostat>> t,int time_start,int time_end,String id_of_room)
    {
        int i;
        System.out.print(room);
        for(i = 0; i < t.size(); i ++) { // loop through every line
            for (int j = 0; j < t.get(i).size(); j++) { // loop through every column
                if ((t.get(i).get(j).device_id.equals(id_of_room)) && (time_start < t.get(i).get(j).time) && (t.get(i).get(j).time < time_end)) {
                    // find required device_id and the required time interval
                    System.out.printf(" %.2f",t.get(i).get(j).temp);
                }
            }
        }
    }

    public static void main(String [] args) throws FileNotFoundException
    {
        java.io.File file = new java.io.File("therm.in");
        PrintStream fileWriter = new PrintStream("therm.out");
        System.setOut(fileWriter);
        Scanner input = new Scanner(file);

        int nr_room,time_reference,surface,time,time_start,time_end,i;
        double temp_global,temp;
        String room = null, device_id,command,command1;

        ArrayList<Room>rooms=new ArrayList<Room>(); // ArrayList for rooms that contains Room objects
        ArrayList<ArrayList<Termostat>> t=new ArrayList<ArrayList<Termostat>>(); // multidimensional ArrayList for termostats

        for(i=0;i<24;i++)
        {
            // initialise 24 lines (time intervals)
            t.add(new ArrayList<Termostat>());
        }

        nr_room=input.nextInt();
        temp_global=input.nextDouble();
        time_reference=input.nextInt();

        for(i=0;i<nr_room;i++)
        {
            room=input.next();
            device_id=input.next();
            surface=input.nextInt();
            Room single_room=new Room(room,device_id,surface);
            rooms.add(single_room);
        }
        while(input.hasNext())
        {
            command=input.next();
            if(command.equals("OBSERVE"))
            {
                device_id=input.next();
                time=input.nextInt();
                temp=input.nextDouble();
                t=Observe(time_reference,time,device_id, temp, t,rooms);
            }
            if(command.equals("LIST"))
            {
                String id_of_room="";
                room=input.next();
                time_start=input.nextInt();
                time_end=input.nextInt();
                for(i=0;i<rooms.size();i++)
                {
                    if(rooms.get(i).room_number.equals(room))
                    {
                        // get the id of the device in that room
                        id_of_room=rooms.get(i).device_id;
                    }
                }
                List(room,t,time_start,time_end,id_of_room);
                System.out.printf("\n");

            }
            if(command.equals("TRIGGER"))
            {
                int j,k,found=0;
                int surf=0;
                double min_temp=100.00,sum_of_min_temp=0,temp_med;
                command1=input.next();
                for(i=0;i<rooms.size();i++)
                {
                    found=0;
                    min_temp=100;
                    surf+=rooms.get(i).surface; // sum of surface
                    for(j = 0; j < t.size(); j ++)
                    {
                        for(k = 0; k < t.get(j).size(); k ++)
                        {
                            if(rooms.get(i).device_id.equals(t.get(j).get(k).device_id))
                            {
                                // find the last observed temp in every room
                                if(min_temp > t.get(j).get(k).temp)
                                {
                                    min_temp=t.get(j).get(k).temp;
                                    found++;
                                }
                            }
                        }
                        if(found!=0)
                        {
                            break;
                        }
                    }
                    sum_of_min_temp+=min_temp * rooms.get(i).surface; // sum of min temperature
                }
                temp_med=sum_of_min_temp/surf; // medium temperature
                if (temp_med < temp_global) {
                    System.out.printf("YES\n");
                }
                else {
                    System.out.printf("NO\n");
                }
            }
            if(command.equals("TEMPERATURE"))
            {
                double temp_glo=input.nextDouble();
                temp_global=temp_glo;
            }
        }
    }
}

