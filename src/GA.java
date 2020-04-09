import classes.City;
import classes.Individual;
import classes.Menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GA {

    private static List<City> cities = new ArrayList<>();

    private static Integer[][] distancesList;

    private static Random random = new Random();

    private static List<Individual> individuals = new ArrayList<>();

    private static Individual bestFitIndividual = null;
    private static Menu menu;


    public static void main(String[] args) {
        System.out.println("-----------Começa-----------");

        int generations = 1;
        Integer unchangedGenerations = 0;
        cities.add(new City(generateId(), "Florianopolis"));
        cities.add(new City(generateId(), "Laguna"));
        cities.add(new City(generateId(), "Criciúma"));
        cities.add(new City(generateId(), "Tubarão"));
        cities.add(new City(generateId(), "Capivaria de baixo"));
        cities.add(new City(generateId(), "Jaguaruna"));
        cities.add(new City(generateId(), "Imbituba"));
        cities.add(new City(generateId(), "Sangão"));
        cities.add(new City(generateId(), "Joinville"));
        cities.add(new City(generateId(), "Gravatal"));

        for (City c: cities) {
            System.out.println(c.getName() + " - " + "ID: " + c.getId() + "\n");
        }
        fillDistances();
        newIndividuals(10, cities.get(0));


        while (generations <= 50 && !unchangedGenerations.equals(25)) {
            unchangedGenerations = stopCriteria(unchangedGenerations);
            generateGenerations();
            generations++;
        }

        System.out.println("-----------RESULTADO-----------");
        individuals.forEach(i -> System.out.println(i.time));
    }

    /**
     *
     * @param id
     * @return
     */
    public static City getCity(Integer id) {
        return cities.stream().filter(c -> c.getId().equals(id)).findFirst().get();
    }
    

    /**
     *
     * @param individual
     */
    public static void calculateTime(Individual individual) {
        Integer time = 0;
        for (int i = 0; i < individual.chromosome.length - 1; i++){

            int line = Integer.parseInt(individual.chromosome[i]);

            int column = Integer.parseInt(individual.chromosome[i + 1]);


            time += distancesList[line -1][column - 1];
        }

        individual.time = time;
    }


    private static void fillDistances() {
        distancesList = new Integer[cities.size()][cities.size()];

        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                if (i == j) {
                    distancesList[i][j] = 0;
                } else if (distancesList[j][i] != null) {
                    distancesList[i][j] = distancesList[j][i];
                } else {
                    distancesList[i][j] = random.nextInt(200) + 1;
                }
            }
        }
            showDistances();
    }



    private static void showDistances() {
        for (int i = 0; i < cities.size(); i++) {
            String result = "";

            for (int j = 0; j < cities.size(); j++) {
                result += distancesList[i][j] + " - ";
            }
            System.out.println(result);
        }
    }
    



    /**
     *
     * @param cityFrom
     */
    public static void newIndividual(City cityFrom) {
        int chromosomeSize = cities.size() + 1;
        Individual individual = new Individual(chromosomeSize);
        List<Integer> randomList = new ArrayList<>();


        randomList.add(cityFrom.getId());

        while (randomList.size() < cities.size()) {
            int randomNumber  = random.nextInt(cities.size()) + 1;
            if (!randomList.contains(randomNumber)) {
                randomList.add(randomNumber);
            }
        }

        for (int i = 0; i < randomList.size(); i++) {
            individual.chromosome[i] = getCity(randomList.get(i)).getId().toString();
        }

        individual.chromosome[cities.size()] = individual.chromosome[0];
        individuals.add(individual);
        calculateTime(individual);
    }

    /**
     *
     * @param individualsNumber
     * @param cityFrom
     */
    public static void newIndividuals(Integer individualsNumber, City cityFrom) {
        for (int i = 0; i < individualsNumber; i++) {
            newIndividual(cityFrom);
        }
    }

    /**
     *
     * @param orderesIndivisualsList
     * @param fitness
     * @return
     */
    public static Individual getBestFitIndividual(List<Individual> orderesIndivisualsList, int fitness) {
        int sum = 0;
        int randomNumber = random.nextInt(fitness) + 1;

        for (int i = 0; i < orderesIndivisualsList.size(); i++){
            sum += orderesIndivisualsList.get(i).fitness;

            if (i == orderesIndivisualsList.size() - 1){
                return orderesIndivisualsList.get(i);
            }

            if (randomNumber >= sum && randomNumber < sum + orderesIndivisualsList.get( i + 1).fitness) {
                return orderesIndivisualsList.get(i);
            }

        }
        throw new RuntimeException("Erro ao selecionar indivíduo mais apto!");

    }

    /**
     *
     * @return
     */
    public  static  List<Individual> getAllBestFitIndividuals() {
        int added = 0;
        List<Individual> orderedIndividuals = individuals.stream().sorted(Comparator.comparing(Individual::getFitness))
                .collect(Collectors.toList());

        List<Individual> orderedTimeInividuals = individuals.stream().sorted(Comparator.comparing(Individual::getTime))
                .collect(Collectors.toList());

        List<Individual> selectedIndividuals = new ArrayList<>();

        selectedIndividuals.add(orderedTimeInividuals.get(0));
        selectedIndividuals.add(orderedTimeInividuals.get(1));

        int fitnessSum = orderedIndividuals.stream().mapToInt(Individual::getFitness).sum();


        while (added < 2) {
            Individual selectedOne = getBestFitIndividual(orderedIndividuals, fitnessSum);

            if (selectedIndividuals.contains(selectedOne)) {
                continue;
            }
            added++;
            selectedIndividuals.add(selectedOne);

        }

        return selectedIndividuals;
    }

    /**
     *
     */
    private static void generateGenerations(){
        fitness();
        List<Individual> bestFitIndividuals = getAllBestFitIndividuals();

        while (bestFitIndividuals.size() < 10) {
            mutation(bestFitIndividuals);
        }
        individuals = new ArrayList<>(bestFitIndividuals);
    }

    public static void fitness() {
        List<Individual> orderedIndividuals = individuals
                .stream().sorted(Comparator.comparing(Individual::getTime).reversed()).collect(Collectors.toList());

        for (int i = 0; i < orderedIndividuals.size(); i++) {
            orderedIndividuals.get(i).fitness = i + 1;
        }
    }

    /**
     *
     * @param chromosome
     * @param firstCutPoint
     * @param secondCutPoint
     */
    private static void mutate(String[] chromosome, int firstCutPoint, int secondCutPoint) {
        String first = chromosome[firstCutPoint];
        String second = chromosome[secondCutPoint];

        chromosome[firstCutPoint] = second;
        chromosome[secondCutPoint] = first;
    }

    private static int generateId(){
        return cities.size() + 1;
    }

    /**
     *
     * @param bestFitIndividuals
     */
    private static void mutation(List<Individual> bestFitIndividuals) {
        Individual individual = bestFitIndividuals.get(0);

        String[] chromosome = individual.chromosome;

        int firstCutPoint  = random.nextInt(individual.chromosome.length - 2) + 1;
        int seconfCutPoint  = random.nextInt(individual.chromosome.length - 2) + 1;


        mutate(chromosome, firstCutPoint, seconfCutPoint);
        Individual newIndividual = new Individual(chromosome);

        calculateTime(newIndividual);
        bestFitIndividuals.add(newIndividual);
    }


    private static int stopCriteria(Integer numberGenerations){
        Individual individual = individuals.stream().min(Comparator.comparing(Individual::getTime)).get();

        if(bestFitIndividual == null){
                bestFitIndividual = individual;
        }
        else if(individual.time >= bestFitIndividual.time) {
            numberGenerations ++;
        }
        else {
            numberGenerations = 0;
            bestFitIndividual = individual;
        }

        return numberGenerations;
    }



    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

}
