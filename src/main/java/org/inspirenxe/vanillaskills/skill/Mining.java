/*
 * This file is part of Vanilla Skills, licensed under the MIT License (MIT).
 *
 * Copyright (c) InspireNXE
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.inspirenxe.vanillaskills.skill;


import static net.kyori.filter.FilterResponse.ALLOW;
import static net.kyori.filter.FilterResponse.DENY;
import static net.kyori.filter.Filters.any;
import static net.kyori.filter.Filters.not;
import static org.inspirenxe.skills.api.skill.builtin.EventProcessors.USER_CHANGE_BLOCK_BREAK;
import static org.inspirenxe.skills.api.skill.builtin.EventProcessors.USER_INTERACT_ITEM;
import static org.inspirenxe.skills.api.skill.builtin.FilterRegistrar.registrar;
import static org.inspirenxe.skills.api.skill.builtin.applicator.EconomyApplicators.scaledMoney;
import static org.inspirenxe.skills.api.skill.builtin.applicator.XPApplicators.xp;
import static org.inspirenxe.skills.api.skill.builtin.block.FuzzyBlockState.state;
import static org.inspirenxe.skills.api.skill.builtin.block.TraitValue.trait;
import static org.inspirenxe.skills.api.skill.builtin.filter.Filters.filters;
import static org.inspirenxe.skills.api.skill.builtin.filter.MatchResponseToFilter.matchTo;
import static org.inspirenxe.skills.api.skill.builtin.filter.RedirectResponseFilter.whenThen;
import static org.inspirenxe.skills.api.skill.builtin.filter.applicator.ApplicatorEntry.applyIf;
import static org.inspirenxe.skills.api.skill.builtin.filter.applicator.TriggerFilter.triggerIf;
import static org.inspirenxe.skills.api.skill.builtin.filter.block.BlockCreatorFilters.natural;
import static org.inspirenxe.skills.api.skill.builtin.filter.block.BlockFilters.blocks;
import static org.inspirenxe.skills.api.skill.builtin.filter.block.BlockFilters.states;
import static org.inspirenxe.skills.api.skill.builtin.filter.gamemode.GameModeFilters.creative;
import static org.inspirenxe.skills.api.skill.builtin.filter.item.ItemFilters.item;
import static org.inspirenxe.skills.api.skill.builtin.filter.level.LevelFilters.level;
import static org.spongepowered.api.block.trait.EnumTraits.STONEBRICK_VARIANT;

import org.inspirenxe.skills.api.effect.firework.FireworkEffectType;
import org.inspirenxe.skills.api.function.economy.EconomyFunctionType;
import org.inspirenxe.skills.api.function.level.LevelFunctionType;
import org.inspirenxe.skills.api.skill.Skill;
import org.inspirenxe.skills.api.skill.builtin.BasicSkillType;
import org.inspirenxe.skills.api.skill.builtin.FilterRegistrar;
import org.inspirenxe.skills.api.skill.holder.SkillHolderContainer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;

public final class Mining extends BasicSkillType {

    public Mining(final PluginContainer container, final String id, final String name, final Text formattedName,
        final LevelFunctionType levelFunction, final int maxLevel) {
        super(container, id, name, formattedName, levelFunction, maxLevel);
    }

    @Override
    public void onConfigure(final Collection<SkillHolderContainer> containers) {
        final EconomyService es = Sponge.getServiceManager().provide(EconomyService.class).orElse(null);
        final EconomyFunctionType ef = Sponge.getRegistry().getType(EconomyFunctionType.class, "vanilla_skills:standard").orElse(null);

        //@formatter:off
        containers.forEach(container -> {
            this
                .register(container, USER_INTERACT_ITEM,
                    registrar()
                        .cancelEvent(
                            matchTo(
                                filters(
                                    creative(),
                                    any(
                                        matchTo(filters(whenThen(filters(item(ItemTypes.STONE_PICKAXE)), ALLOW, DENY), level(15)), DENY),
                                        matchTo(filters(whenThen(filters(item(ItemTypes.IRON_PICKAXE)), ALLOW, DENY), level(30)), DENY),
                                        matchTo(filters(whenThen(filters(item(ItemTypes.GOLDEN_PICKAXE)), ALLOW, DENY), level(45)), DENY),
                                        matchTo(filters(whenThen(filters(item(ItemTypes.DIAMOND_PICKAXE)), ALLOW, DENY), level(60)), DENY)
                                    )
                                ),
                                DENY
                            )
                        )
                        .build()
                );

            final FilterRegistrar.Builder breakBlock =
                registrar()
                    .cancelTransaction(
                        matchTo(
                            filters(
                                creative(),
                                any(
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.SANDSTONE)), ALLOW, DENY), level(10)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.COAL_ORE)), ALLOW, DENY), level(15)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.STONEBRICK)), ALLOW, DENY), level(15)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.IRON_ORE)), ALLOW, DENY), level(25)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.LAPIS_ORE)), ALLOW, DENY), level(35)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.GOLD_ORE)), ALLOW, DENY), level(45)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.DIAMOND_ORE)), ALLOW, DENY), level(55)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.OBSIDIAN)), ALLOW, DENY), level(60)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.NETHERRACK)), ALLOW, DENY), level(75)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.END_STONE)), ALLOW, DENY), level(85)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.EMERALD_ORE)), ALLOW, DENY), level(95)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.LIT_REDSTONE_ORE)), ALLOW, DENY), level(99)), DENY),
                                    matchTo(filters(whenThen(filters(blocks(BlockTypes.REDSTONE_ORE)), ALLOW, DENY), level(99)), DENY)
                                )
                            ),
                            DENY
                        )
                    )
                    .transactionTrigger(
                        triggerIf()
                            .all(not(creative()), blocks(), natural())
                            .then(
                                applyIf().any(blocks(BlockTypes.STONE)).then(xp(5)).build(),
                                applyIf().any(blocks(BlockTypes.NETHERRACK)).then(xp(2.5)).build(),
                                applyIf().any(blocks(BlockTypes.SANDSTONE)).then(xp(4)).build(),
                                applyIf().any(blocks(BlockTypes.COAL_ORE)).then(xp(8)).build(),
                                applyIf().any(blocks(BlockTypes.IRON_ORE)).then(xp(15)).build(),
                                applyIf().any(blocks(BlockTypes.LAPIS_ORE)).then(xp(15)).build(),
                                applyIf().any(blocks(BlockTypes.GOLD_ORE)).then(xp(25)).build(),
                                applyIf().any(blocks(BlockTypes.REDSTONE_ORE)).then(xp(30)).build(),
                                applyIf().any(blocks(BlockTypes.LIT_REDSTONE_ORE)).then(xp(35)).build(),
                                applyIf().any(blocks(BlockTypes.DIAMOND_ORE)).then(xp(35)).build(),
                                applyIf().any(blocks(BlockTypes.END_STONE)).then(xp(35)).build(),
                                applyIf().any(blocks(BlockTypes.OBSIDIAN)).then(xp(40)).build(),
                                applyIf().any(blocks(BlockTypes.EMERALD_ORE)).then(xp(50)).build(),
                                applyIf().any(states(state(BlockTypes.STONEBRICK, trait(STONEBRICK_VARIANT, "stonebrick")))).then(xp(4)).build(),
                                applyIf().any(states(state(BlockTypes.STONEBRICK, trait(STONEBRICK_VARIANT, "mossy_stonebrick")))).then(xp(7)).build(),
                                applyIf().any(states(state(BlockTypes.STONEBRICK, trait(STONEBRICK_VARIANT, "cracked_stonebrick")))).then(xp(15)).build()
                            )
                            .elseApply(xp(0.1))
                            .build()
                    );


            if (es != null && ef != null) {
                final Currency c = es.getDefaultCurrency();
                
                breakBlock
                    .transactionTrigger(
                        triggerIf()
                            .all(not(creative()), blocks(), natural())
                            .then(
                                applyIf().any(blocks(BlockTypes.STONE)).then(scaledMoney(es, ef, c, 1)).build(),
                                applyIf().any(blocks(BlockTypes.NETHERRACK)).then(scaledMoney(es, ef, c, 0.9)).build(),
                                applyIf().any(blocks(BlockTypes.SANDSTONE)).then(scaledMoney(es, ef, c, 2)).build(),
                                applyIf().any(blocks(BlockTypes.COAL_ORE)).then(scaledMoney(es, ef, c, 3)).build(),
                                applyIf().any(blocks(BlockTypes.IRON_ORE)).then(scaledMoney(es, ef, c, 4)).build(),
                                applyIf().any(blocks(BlockTypes.LAPIS_ORE)).then(scaledMoney(es, ef, c, 5)).build(),
                                applyIf().any(blocks(BlockTypes.GOLD_ORE)).then(scaledMoney(es, ef, c, 6)).build(),
                                applyIf().any(blocks(BlockTypes.REDSTONE_ORE)).then(scaledMoney(es, ef, c, 7)).build(),
                                applyIf().any(blocks(BlockTypes.LIT_REDSTONE_ORE)).then(scaledMoney(es, ef, c, 7)).build(),
                                applyIf().any(blocks(BlockTypes.DIAMOND_ORE)).then(scaledMoney(es, ef, c, 8)).build(),
                                applyIf().any(blocks(BlockTypes.END_STONE)).then(scaledMoney(es, ef, c, 1)).build(),
                                applyIf().any(blocks(BlockTypes.OBSIDIAN)).then(scaledMoney(es, ef, c, 8)).build(),
                                applyIf().any(blocks(BlockTypes.EMERALD_ORE)).then(scaledMoney(es, ef, c, 10)).build(),
                                applyIf().any(states(state(BlockTypes.STONEBRICK, trait(STONEBRICK_VARIANT, "stonebrick")))).then(scaledMoney(es, ef, c, 4)).build(),
                                applyIf().any(states(state(BlockTypes.STONEBRICK, trait(STONEBRICK_VARIANT, "mossy_stonebrick")))).then(scaledMoney(es, ef, c, 5)).build(),
                                applyIf().any(states(state(BlockTypes.STONEBRICK, trait(STONEBRICK_VARIANT, "cracked_stonebrick")))).then(scaledMoney(es, ef, c, 5)).build()
                            )
                            .elseApply(scaledMoney(es, ef, c, 0.1))
                            .build()
                    );
            }

            this.register(container, USER_CHANGE_BLOCK_BREAK, breakBlock.build());
        });
        //@formatter:on
    }

    @Override
    public void onXPChanged(final Cause cause, final Skill skill, final double amount) {
    }

    @Override
    public void onLevelChanged(final Cause cause, final Skill skill, final int newLevel) {
    }

    @Override
    public Optional<FireworkEffectType> getFireworkEffectFor(final int level) {
        return super.getFireworkEffectFor(level);
    }
}
