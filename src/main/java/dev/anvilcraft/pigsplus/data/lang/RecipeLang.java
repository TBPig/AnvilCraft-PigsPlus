package dev.anvilcraft.pigsplus.data.lang;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class RecipeLang {
    public static void init(RegistrumLangProvider provider) {
        provider.add("modification.anvilcraft_pigsplus.unknown", "Unknown Modification");
        provider.add("modification.anvilcraft_pigsplus.slow_rotation", "Slow Down Rotation");
        provider.add("modification.anvilcraft_pigsplus.fast_rotation", "Speed Up Rotation");
        provider.add("modification.anvilcraft_pigsplus.strengthen_magnetic_field", "Strengthen Magnetic Field");
        provider.add("modification.anvilcraft_pigsplus.weaken_magnetic_field", "Weaken Magnetic Field");
        provider.add("modification.anvilcraft_pigsplus.increase_ocean_coverage", "Increase Ocean Coverage: %s");
        provider.add("modification.anvilcraft_pigsplus.decrease_liquid_coverage", "Decrease Ocean Coverage");
        provider.add("modification.anvilcraft_pigsplus.add_atmosphere", "Add Atmosphere");
        provider.add("modification.anvilcraft_pigsplus.increase_temperature", "Increase Temperature");
        provider.add("modification.anvilcraft_pigsplus.decrease_temperature", "Decrease Temperature");
        provider.add("modification.anvilcraft_pigsplus.add_biological_resources", "Add Biological Resources");
        provider.add("modification.anvilcraft_pigsplus.add_civilization", "Add Low Civilization");
        provider.add("modification.anvilcraft_pigsplus.wasteland", "Turn Into Wasteland");
        provider.add("modification.anvilcraft_pigsplus.void_wasteland", "Turn Into Void Wasteland");
        provider.add("modification.anvilcraft_pigsplus.special_celestial_body", "Transform Into %s");

        provider.add("concept.anvilcraft_pigsplus.rotation_speed", "Rotation Speed");
        provider.add("concept.anvilcraft_pigsplus.magnetic_field", "Magnetic Field");
        provider.add("concept.anvilcraft_pigsplus.sea", "Liquid Coverage");
        provider.add("concept.anvilcraft_pigsplus.temperature", "Temperature");
        provider.add("concept.anvilcraft_pigsplus.atmosphere", "Atmosphere");
        provider.add("concept.anvilcraft_pigsplus.biologic", "Biological Resources");
        provider.add("concept.anvilcraft_pigsplus.civilization", "Civilization");
        provider.add("concept.anvilcraft_pigsplus.plante_type", "Planet Type");
        provider.add("concept.anvilcraft_pigsplus.reformer", "Reformer");
        provider.add("concept.anvilcraft_pigsplus.laser", "Laser");
        provider.add("concept.anvilcraft_pigsplus.default", "Unknown Concept");

        provider.add("requirement.anvilcraft_pigsplus.unknown", "Unknown Requirement");
        provider.add("requirement.anvilcraft_pigsplus.star_reformer", "Requires Star Reformer");
        provider.add("requirement.anvilcraft_pigsplus.planetary_reformer", "Requires Planetary Reformer");
        provider.add("requirement.anvilcraft_pigsplus.has_liquid", "Requires Liquid");
        provider.add("requirement.anvilcraft_pigsplus.no_liquid", "Requires No Liquid");
        provider.add("requirement.anvilcraft_pigsplus.has_atmosphere", "Requires Atmosphere");
        provider.add("requirement.anvilcraft_pigsplus.no_atmosphere", "Requires No Atmosphere");
        provider.add("requirement.anvilcraft_pigsplus.rotation_speed_at_least", "Rotation Speed >= %s");
        provider.add("requirement.anvilcraft_pigsplus.rotation_speed_at_most", "Rotation Speed <= %s");
        provider.add("requirement.anvilcraft_pigsplus.magnetic_field_at_least", "Magnetic Field >= %s");
        provider.add("requirement.anvilcraft_pigsplus.magnetic_field_at_most", "Magnetic Field <= %s");
        provider.add("requirement.anvilcraft_pigsplus.magnetic_field_between", "Magnetic Field >= %s and <= %s");
        provider.add("requirement.anvilcraft_pigsplus.rotation_speed_between", "Rotation Speed >= %s and <= %s");
        provider.add("requirement.anvilcraft_pigsplus.has_other_megastructure", "Requires Another Megastructure");
        provider.add("requirement.anvilcraft_pigsplus.no_other_megastructure", "Requires No Other Megastructure");
        provider.add("requirement.anvilcraft_pigsplus.rocky_planet", "Requires Rocky Planet");
        provider.add("requirement.anvilcraft_pigsplus.water_ocean", "Requires No Liquid Or Water Ocean Below Full Coverage");
        provider.add("requirement.anvilcraft_pigsplus.ocean_liquid", "Requires No Liquid Or %s Ocean Below Full Coverage");
        provider.add("requirement.anvilcraft_pigsplus.temperature_at_least", "Temperature >= %s");
        provider.add("requirement.anvilcraft_pigsplus.temperature_at_most", "Temperature <= %s");
        provider.add("requirement.anvilcraft_pigsplus.temperature_between", "Temperature %s - %s");
        provider.add("requirement.anvilcraft_pigsplus.liquid_coverage_at_least", "Liquid Coverage >= %s");
        provider.add("requirement.anvilcraft_pigsplus.liquid_coverage_at_most", "Liquid Coverage <= %s");
        provider.add("requirement.anvilcraft_pigsplus.liquid_coverage_between", "Liquid Coverage %s - %s");
        provider.add("requirement.anvilcraft_pigsplus.has_biological_resources", "Requires Biological Resources");
        provider.add("requirement.anvilcraft_pigsplus.has_civilization", "Requires Civilization");
    }
}
