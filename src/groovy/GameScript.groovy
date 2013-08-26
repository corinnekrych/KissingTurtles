
abstract class GameScript extends Script {

    def propertyMissing(name, args) {
        if(name=="kiss") {
            binding.turtle.kiss()
        }
    }
}
