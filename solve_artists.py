from itertools import permutations

names = ['Kimberly','Margaret','Charlotte','Yasmin','Other']
sneakers = ['Yellow','Pink','Blue','Purple','Green']
locations = ['alley','warehouse','park','bridge','studio']
themes = ['People','Symbols','Animals','Culture','Nature']
styles = ['Geometric','Street','Surreal','Stencil','Pop']
ages = ['youngest','22','26','32','oldest']

solutions = []

for name_perm in permutations(names):
    # constraints involving names will be checked later
    for sneak_perm in permutations(sneakers):
        # Yellow is Margaret
        pos_m = name_perm.index('Margaret')
        if sneak_perm[pos_m] != 'Yellow':
            continue
        # Pink is Charlotte
        pos_c = name_perm.index('Charlotte')
        if sneak_perm[pos_c] != 'Pink':
            continue
        # Green is Kimberly
        pos_k = name_perm.index('Kimberly')
        if sneak_perm[pos_k] != 'Green':
            continue
        for loc_perm in permutations(locations):
            # Green -> park
            if loc_perm[pos_k] != 'park':
                continue
            # warehouse must be to the right of green
            pos_warehouse = loc_perm.index('warehouse')
            if pos_warehouse <= pos_k:
                continue
            # bridge immediately after warehouse
            if pos_warehouse == 4:
                continue
            if loc_perm[pos_warehouse+1] != 'bridge':
                continue
            for theme_perm in permutations(themes):
                # Charlotte -> Symbols
                if theme_perm[pos_c] != 'Symbols':
                    continue
                # park -> Culture
                if theme_perm[pos_k] != 'Culture':
                    continue
                # alley -> People
                pos_alley = loc_perm.index('alley')
                if theme_perm[pos_alley] != 'People':
                    continue
                # Yasmin -> Animals
                pos_y = name_perm.index('Yasmin')
                if theme_perm[pos_y] != 'Animals':
                    continue
                for style_perm in permutations(styles):
                    # Symbols -> Geometric
                    if style_perm[pos_c] != 'Geometric':
                        continue
                    # Geometric on an end
                    if style_perm[0] != 'Geometric' and style_perm[4] != 'Geometric':
                        continue
                    # Surreal on an end
                    if style_perm[0] != 'Surreal' and style_perm[4] != 'Surreal':
                        continue
                    # Pop adjacent to Geometric
                    pos_geo = 0 if style_perm[0]=='Geometric' else 4
                    if pos_geo==0:
                        if style_perm[1] != 'Pop':
                            continue
                    else:
                        if style_perm[3] != 'Pop':
                            continue
                    # Street adjacent to warehouse
                    pos_street = style_perm.index('Street')
                    if abs(pos_street - pos_warehouse) != 1:
                        continue
                    # Stencil between Animals and Purple in that order
                    pos_anim = theme_perm.index('Animals')
                    try:
                        pos_stencil = style_perm.index('Stencil')
                        pos_purple = sneak_perm.index('Purple')
                    except ValueError:
                        continue
                    if not (pos_anim < pos_stencil < pos_purple):
                        continue
                    for age_perm in permutations(ages):
                        # Kimberly youngest
                        if age_perm[pos_k] != 'youngest':
                            continue
                        # Margaret oldest
                        if age_perm[pos_m] != 'oldest':
                            continue
                        # Charlotte 26
                        if age_perm[pos_c] != '26':
                            continue
                        # Blue sneaker wearer is 22
                        pos_blue = sneak_perm.index('Blue')
                        if age_perm[pos_blue] != '22':
                            continue
                        # Symbols 26 already done
                        # 32 between 22 and oldest
                        pos_22 = age_perm.index('22')
                        pos_32 = age_perm.index('32')
                        pos_old = age_perm.index('oldest')
                        if not (pos_22 < pos_32 < pos_old):
                            continue
                        # All constraints satisfied
                        sol = []
                        for i in range(5):
                            sol.append((i+1,name_perm[i],sneak_perm[i],loc_perm[i],theme_perm[i],style_perm[i],age_perm[i]))
                        solutions.append(sol)

print('Found',len(solutions),'solutions')
for sol in solutions:
    for s in sol:
        print(s)
    print('---')
